package com.example.snakebackend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.snakebackend.models.NewsPost;

/**
 * Data access for {@link NewsPost}. Spring Data JPA implements the
 * derived queries from their method names; the explicit @Query methods
 * below back the search + atomic-like-increment endpoints.
 */
public interface NewsRepo extends JpaRepository<NewsPost, Long> {

    /** All posts newest-first (for the full News tab). */
    List<NewsPost> findAllByOrderByPostedAtDesc();

    /** The N most recent posts (used by the home-page preview). */
    List<NewsPost> findTop6ByOrderByPostedAtDesc();

    /** Filter by category tag, newest first (for tab filtering). */
    List<NewsPost> findByCategoryOrderByPostedAtDesc(String category);

    /**
     * Top N posts by likes — drives the "trending" rail on the news
     * page. Uses a Pageable would be neater, but Top10 keeps it
     * derived-query simple.
     */
    List<NewsPost> findTop10ByOrderByLikesDescPostedAtDesc();

    /**
     * Case-insensitive keyword search across title, summary and body.
     * Newest match first when scores tie.
     */
    @Query("""
            SELECT n FROM NewsPost n
            WHERE LOWER(n.title)   LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(n.summary) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(n.body)    LIKE LOWER(CONCAT('%', :q, '%'))
            ORDER BY n.postedAt DESC
           """)
    List<NewsPost> search(@Param("q") String q);

    /**
     * Count posts grouped by category, alphabetical. Each row is a
     * 2-element Object[] of (category, count) — the service maps it
     * into a clean DTO before returning to the controller.
     */
    @Query("""
            SELECT n.category, COUNT(n) FROM NewsPost n
            GROUP BY n.category
            ORDER BY n.category
           """)
    List<Object[]> countByCategoryRaw();

    /**
     * Atomic +1 to the likes counter — avoids a read-modify-write race
     * if two browsers like the same article in the same millisecond.
     * Returns the number of rows updated (0 if the post doesn't exist).
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE NewsPost n SET n.likes = n.likes + 1 WHERE n.id = :id")
    int incrementLikes(@Param("id") Long id);
}
