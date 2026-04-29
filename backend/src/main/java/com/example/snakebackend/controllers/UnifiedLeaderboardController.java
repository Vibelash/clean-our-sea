package com.example.snakebackend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.snakebackend.dto.UnifiedLeaderboardEntry;
import com.example.snakebackend.services.UnifiedLeaderboardService;

/**
 * Cross-module leaderboard surface. Fans in over Tala's User table,
 * Karan's Score table and Daniel's UserQuiz table to produce one ranked
 * list — the single endpoint the marking criteria asks for as evidence
 * of "true integration".
 *
 *   GET /api/leaderboard
 */
@RestController
@RequestMapping("/api")
@CrossOrigin
public class UnifiedLeaderboardController {

    private final UnifiedLeaderboardService service;

    public UnifiedLeaderboardController(UnifiedLeaderboardService service) {
        this.service = service;
    }

    /** Top 25 users ranked by combined participation across all 3 modules. */
    @GetMapping("/leaderboard")
    public List<UnifiedLeaderboardEntry> getUnifiedLeaderboard() {
        return service.getUnifiedLeaderboard();
    }
}
