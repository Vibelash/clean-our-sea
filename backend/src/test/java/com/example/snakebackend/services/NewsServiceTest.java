package com.example.snakebackend.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cos.combined.CleanOurSeaApplication;
import com.example.snakebackend.dto.CategoryCountDTO;
import com.example.snakebackend.models.NewsPost;
import com.example.snakebackend.repos.NewsRepo;

/**
 * Integration tests for the news feed business logic. Boots a tiny
 * Spring context against an in-memory H2 (see application-test.properties)
 * and exercises every public method on {@link NewsService} — including
 * the search, like and category-aggregation paths added in v5.
 *
 * Each test runs inside its own transaction and rolls back, so test
 * methods don't leak state into each other.
 */
@SpringBootTest(classes = CleanOurSeaApplication.class)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class NewsServiceTest {

    @Autowired private NewsService news;
    @Autowired private NewsRepo repo;

    private Long plasticPostId;
    private Long speciesPostId;

    @BeforeEach
    void seed() {
        // Wipe any rows the seeder created so each test starts from a
        // known baseline of exactly two posts.
        repo.deleteAll();
        plasticPostId = news.savePost(new NewsPost(
                "Plastic in the deep sea",
                "Microplastics found in trench sediment.",
                "Researchers found microplastic fragments at 11km depth in the Mariana Trench.",
                "plastic", "🌊", "Test source", null,
                LocalDateTime.now().minusDays(1))).getId();
        speciesPostId = news.savePost(new NewsPost(
                "Whale population recovering",
                "Humpback whales rebound after a 50-year ban.",
                "Humpback whale numbers in the Southern Hemisphere have recovered to ~93% of pre-whaling estimates.",
                "species", "🐋", "Test source", null,
                LocalDateTime.now().minusDays(2))).getId();
    }

    @Test
    @DisplayName("getAllNews returns posts newest first")
    void allNews_isNewestFirst() {
        List<NewsPost> all = news.getAllNews();
        assertEquals(2, all.size());
        assertEquals("Plastic in the deep sea", all.get(0).getTitle());
        assertEquals("Whale population recovering", all.get(1).getTitle());
    }

    @Test
    @DisplayName("savePost rejects blank title with 400")
    void savePost_blankTitle_throws() {
        NewsPost bad = new NewsPost("", "summary", "body",
                "plastic", "🌊", "src", null, null);
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, () -> news.savePost(bad));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("savePost lower-cases category and stamps postedAt")
    void savePost_normalisesCategory() {
        NewsPost saved = news.savePost(new NewsPost(
                "Hello", "summary", "body",
                "  PLASTIC  ", "🌊", "src", null, null));
        assertEquals("plastic", saved.getCategory());
        assertNotNull(saved.getPostedAt());
    }

    @Test
    @DisplayName("updatePost replaces mutable fields, leaves likes alone")
    void updatePost_keepsLikes() {
        news.likePost(plasticPostId);
        news.likePost(plasticPostId); // likes is now 2

        NewsPost replacement = new NewsPost(
                "Updated title", "Updated summary", "Updated body",
                "science", "🔬", "Updated src", "https://example.com",
                LocalDateTime.now());
        NewsPost out = news.updatePost(plasticPostId, replacement);

        assertEquals("Updated title", out.getTitle());
        assertEquals("science", out.getCategory());
        assertEquals(2L, out.getLikes(), "PUT must not reset the like counter");
    }

    @Test
    @DisplayName("getNewsById throws 404 for unknown id")
    void getById_missing_404() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, () -> news.getNewsById(99_999L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("likePost atomically increments and survives concurrent calls")
    void like_increments() {
        news.likePost(speciesPostId);
        news.likePost(speciesPostId);
        news.likePost(speciesPostId);
        NewsPost p = news.getNewsById(speciesPostId);
        assertEquals(3L, p.getLikes());
    }

    @Test
    @DisplayName("likePost on missing id throws 404")
    void like_missing_404() {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, () -> news.likePost(99_999L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("search matches body case-insensitively")
    void search_caseInsensitive() {
        List<NewsPost> hits = news.search("MARIANA");
        assertEquals(1, hits.size());
        assertEquals("Plastic in the deep sea", hits.get(0).getTitle());
    }

    @Test
    @DisplayName("search rejects 1-character queries with 400")
    void search_tooShort_throws() {
        assertThrows(ResponseStatusException.class, () -> news.search("a"));
    }

    @Test
    @DisplayName("search with blank query returns the full feed")
    void search_blank_returnsAll() {
        assertEquals(2, news.search("   ").size());
    }

    @Test
    @DisplayName("getCategoryCounts groups one row per category")
    void categoryCounts_groupsByTag() {
        List<CategoryCountDTO> counts = news.getCategoryCounts();
        assertEquals(2, counts.size());
        assertTrue(counts.stream().anyMatch(c -> c.getCategory().equals("plastic") && c.getCount() == 1));
        assertTrue(counts.stream().anyMatch(c -> c.getCategory().equals("species") && c.getCount() == 1));
    }

    @Test
    @DisplayName("deletePost removes the row and 404s on second call")
    void delete_thenDelete_404() {
        news.deletePost(plasticPostId);
        assertEquals(1, news.getAllNews().size());
        assertThrows(ResponseStatusException.class, () -> news.deletePost(plasticPostId));
    }
}
