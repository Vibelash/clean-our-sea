package com.example.snakebackend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.snakebackend.dto.CategoryCountDTO;
import com.example.snakebackend.models.NewsPost;
import com.example.snakebackend.repos.NewsRepo;

/**
 * Business layer for the News & Facts feed. Controller → Service →
 * Repo, same three-tier shape as ScoreService. Keeps validation,
 * the "latest N" logic, search, and the category aggregation out of
 * the controller.
 */
@Service
public class NewsService {

    /** How many posts the home-page preview section shows. */
    private static final int HOME_PREVIEW_SIZE = 6;

    /** Minimum length of a search query — single-letter searches return everything otherwise. */
    private static final int MIN_SEARCH_LENGTH = 2;

    private final NewsRepo repo;

    public NewsService(NewsRepo repo) {
        this.repo = repo;
    }

    // ---------- Read ----------

    /** Full feed, newest first — powers the News tab. */
    public List<NewsPost> getAllNews() {
        return repo.findAllByOrderByPostedAtDesc();
    }

    /** Latest 6 posts for the home-page preview strip. */
    public List<NewsPost> getHomePreview() {
        List<NewsPost> latest = repo.findTop6ByOrderByPostedAtDesc();
        if (latest.size() <= HOME_PREVIEW_SIZE) return latest;
        return latest.subList(0, HOME_PREVIEW_SIZE);
    }

    /** One specific post for a "read more" detail view. */
    public NewsPost getNewsById(Long id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No news post with id " + id));
    }

    /** Filter posts by category tag (e.g. "plastic", "species"). */
    public List<NewsPost> getNewsByCategory(String category) {
        if (category == null || category.isBlank()) {
            return getAllNews();
        }
        return repo.findByCategoryOrderByPostedAtDesc(category.trim().toLowerCase());
    }

    /**
     * Top 10 most-liked posts. Drives the "trending" rail on the home
     * page once readers start interacting with articles.
     */
    public List<NewsPost> getTrending() {
        return repo.findTop10ByOrderByLikesDescPostedAtDesc();
    }

    /**
     * Free-text search over title, summary and body. Empty / blank
     * queries fall back to the full feed; very short queries (1 char)
     * are rejected to avoid expensive scans.
     */
    public List<NewsPost> search(String query) {
        if (query == null || query.isBlank()) {
            return getAllNews();
        }
        String trimmed = query.trim();
        if (trimmed.length() < MIN_SEARCH_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Search query must be at least " + MIN_SEARCH_LENGTH + " characters");
        }
        return repo.search(trimmed);
    }

    /**
     * Aggregate counts per category. Maps the raw Object[] rows from
     * the repo into typed DTOs so the controller can return them
     * directly as JSON.
     */
    public List<CategoryCountDTO> getCategoryCounts() {
        return repo.countByCategoryRaw().stream()
                .map(row -> new CategoryCountDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue()))
                .toList();
    }

    // ---------- Write ----------

    /**
     * Create a new post. Stamps {@code postedAt} with the current
     * time if the caller didn't provide one. Rejects blank titles.
     */
    public NewsPost savePost(NewsPost post) {
        validate(post);
        if (post.getCategory() == null || post.getCategory().isBlank()) {
            post.setCategory("general");
        }
        post.setCategory(post.getCategory().trim().toLowerCase());
        if (post.getPostedAt() == null) {
            post.setPostedAt(LocalDateTime.now());
        }
        return repo.save(post);
    }

    /**
     * Replace mutable fields on an existing post. Likes and id are
     * never touched here — likes have their own endpoint, and the id
     * is the lookup key.
     */
    public NewsPost updatePost(Long id, NewsPost incoming) {
        NewsPost existing = getNewsById(id);
        validate(incoming);
        existing.setTitle(incoming.getTitle().trim());
        existing.setSummary(incoming.getSummary().trim());
        existing.setBody(incoming.getBody().trim());
        if (incoming.getCategory() != null && !incoming.getCategory().isBlank()) {
            existing.setCategory(incoming.getCategory().trim().toLowerCase());
        }
        existing.setIcon(incoming.getIcon());
        existing.setSource(incoming.getSource());
        existing.setSourceUrl(incoming.getSourceUrl());
        if (incoming.getPostedAt() != null) {
            existing.setPostedAt(incoming.getPostedAt());
        }
        return repo.save(existing);
    }

    public void deletePost(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No news post with id " + id);
        }
        repo.deleteById(id);
    }

    /**
     * Atomic +1 like. Returns the post with the new total so the
     * frontend can show the updated counter without a second GET.
     * Marked @Transactional because the repo's @Modifying query
     * needs an active transaction to flush.
     */
    @Transactional
    public NewsPost likePost(Long id) {
        int updated = repo.incrementLikes(id);
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No news post with id " + id);
        }
        // Re-read so the response reflects the new like count.
        return getNewsById(id);
    }

    /** How many posts are in the feed. Used by the home-page stat pill. */
    public long count() {
        return repo.count();
    }

    // ---------- helpers ----------

    private void validate(NewsPost post) {
        if (post == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Post payload is missing");
        }
        if (post.getTitle() == null || post.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Title is required");
        }
        if (post.getSummary() == null || post.getSummary().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Summary is required");
        }
        if (post.getBody() == null || post.getBody().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Body is required");
        }
    }
}
