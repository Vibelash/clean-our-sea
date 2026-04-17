package com.example.snakebackend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.snakebackend.models.NewsPost;
import com.example.snakebackend.services.NewsService;

/**
 * REST surface for the News & Facts feed. Mirrors the thin-routing
 * shape of {@link ScoreController} — no logic lives here.
 *
 *  GET    /news                → full list, newest first
 *  GET    /news/preview        → 3 most recent (home page)
 *  GET    /news/{id}           → one post
 *  GET    /news?category=plastic → filtered feed
 *  POST   /news                → add a new post (admin / seed)
 *  DELETE /news/{id}           → remove a post
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

    /** Top 3 most recent posts, used by the home-page preview strip. */
    @GetMapping("/preview")
    public List<NewsPost> getHomePreview() {
        return service.getHomePreview();
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

    /** Remove a post. 404 if it doesn't exist. */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deletePost(id);
    }
}
