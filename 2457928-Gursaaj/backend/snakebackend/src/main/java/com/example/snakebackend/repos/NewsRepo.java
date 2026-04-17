package com.example.snakebackend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.snakebackend.models.NewsPost;

/**
 * Data access for {@link NewsPost}. Spring Data JPA implements the
 * derived queries from their method names — no SQL needed.
 */
public interface NewsRepo extends JpaRepository<NewsPost, Long> {

    /** All posts newest-first (for the full News tab). */
    List<NewsPost> findAllByOrderByPostedAtDesc();

    /** The N most recent posts (used by the home-page preview). */
    List<NewsPost> findTop6ByOrderByPostedAtDesc();

    /** Filter by category tag, newest first (for tab filtering). */
    List<NewsPost> findByCategoryOrderByPostedAtDesc(String category);
}
