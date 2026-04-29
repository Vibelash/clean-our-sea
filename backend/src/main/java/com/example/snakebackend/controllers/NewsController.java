package com.example.snakebackend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.snakebackend.dto.CategoryCountDTO;
import com.example.snakebackend.models.NewsPost;
import com.example.snakebackend.services.NewsService;

/**
 * REST surface for the News & Facts feed. Mirrors the thin-routing
 * shape of {@link ScoreController} — no business logic lives here.
 *
 *  GET    /news                     full list, newest first
 *  GET    /news/preview             3 most recent (home page)
 *  GET    /news/trending            top 10 by likes
 *  GET    /news/categories          counts per category
 *  GET    /news/search?q=keyword    keyword search across title/summary/body
 *  GET    /news?category=plastic    filtered feed
 *  GET    /news/{id}                one post
 *  POST   /news                     create a post
 *  PUT    /news/{id}                replace a post's mutable fields
 *  PATCH  /news/{id}/like           atomic +1 to the likes counter
 *  DELETE /news/{id}                remove a post
 */
@RestController
@RequestMapping("/news")
@CrossOrigin
public class NewsController {

    private final NewsService service;

    public NewsController(NewsService service) {
        this.service = service;
    }

    /** Full feed, newest first. Optional ?category=plastic filter. */
    @GetMapping
    public List<NewsPost> getAll(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return service.getNewsByCategory(category);
        }
        return service.getAllNews();
    }

    /** Top 6 most recent posts, used by the home-page preview strip. */
    @GetMapping("/preview")
    public List<NewsPost> getHomePreview() {
        return service.getHomePreview();
    }

    /** Top 10 most-liked posts. Reader-driven ordering, ties broken by recency. */
    @GetMapping("/trending")
    public List<NewsPost> getTrending() {
        return service.getTrending();
    }

    /** Aggregated count of posts per category. */
    @GetMapping("/categories")
    public List<CategoryCountDTO> getCategoryCounts() {
        return service.getCategoryCounts();
    }

    /** Free-text search across title, summary and body. */
    @GetMapping("/search")
    public List<NewsPost> search(@RequestParam("q") String q) {
        return service.search(q);
    }

    /** Read one post by id. 404 if missing. */
    @GetMapping("/{id}")
    public NewsPost getOne(@PathVariable Long id) {
        return service.getNewsById(id);
    }

    /** Create a new post. Returns the saved row (with id + postedAt). */
    @PostMapping
    public NewsPost add(@RequestBody NewsPost post) {
        return service.savePost(post);
    }

    /** Replace the mutable fields of an existing post. */
    @PutMapping("/{id}")
    public NewsPost update(@PathVariable Long id, @RequestBody NewsPost post) {
        return service.updatePost(id, post);
    }

    /** Atomic +1 to the likes counter. Idempotent at the DB level. */
    @PatchMapping("/{id}/like")
    public NewsPost like(@PathVariable Long id) {
        return service.likePost(id);
    }

    /** Remove a post. 404 if it doesn't exist. */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePost(id);
    }
}
