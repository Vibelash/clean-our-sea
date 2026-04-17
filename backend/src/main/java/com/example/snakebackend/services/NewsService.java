package com.example.snakebackend.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.snakebackend.models.NewsPost;
import com.example.snakebackend.repos.NewsRepo;

/**
 * Business layer for the News & Facts feed. Controller → Service →
 * Repo, same three-tier shape as ScoreService. Keeps validation and
 * the "latest N" logic out of the controller.
 */
@Service
public class NewsService {

    /** How many posts the home-page preview section shows. */
    private static final int HOME_PREVIEW_SIZE = 6;

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

    // ---------- Write ----------

    /**
     * Create a new post. Stamps {@code postedAt} with the current
     * time if the caller didn't provide one. Rejects blank titles.
     */
    public NewsPost savePost(NewsPost post) {
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
        if (post.getCategory() == null || post.getCategory().isBlank()) {
            post.setCategory("general");
        }
        post.setCategory(post.getCategory().trim().toLowerCase());
        if (post.getPostedAt() == null) {
            post.setPostedAt(LocalDateTime.now());
        }
        return repo.save(post);
    }

    public void deletePost(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No news post with id " + id);
        }
        repo.deleteById(id);
    }

    /** How many posts are in the feed. Used by the home-page stat pill. */
    public long count() {
        return repo.count();
    }
}
