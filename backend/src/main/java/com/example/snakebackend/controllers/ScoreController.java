package com.example.snakebackend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.snakebackend.dto.ImpactDTO;
import com.example.snakebackend.dto.PlayerStatsDTO;
import com.example.snakebackend.models.Score;
import com.example.snakebackend.services.ScoreService;

/**
 * REST API surface for the Snake Infinity scoreboard.
 *
 * This class is intentionally a thin routing layer: it maps URLs to
 * {@link ScoreService} calls and does no logic of its own. All validation,
 * aggregation, and derived data live in the service.
 */
@RestController
@RequestMapping("/scores")
@CrossOrigin
public class ScoreController {

    private final ScoreService service;

    public ScoreController(ScoreService service) {
        this.service = service;
    }

    // ---------- CRUD ----------

    /** Read — list every score in the database. */
    @GetMapping
    public List<Score> getScores() {
        return service.getAllScores();
    }

    /** Read — fetch a single score by id. 404 if missing. */
    @GetMapping("/{id}")
    public Score getScore(@PathVariable Long id) {
        return service.getScoreById(id);
    }

    /** Create — called by the frontend on game over. */
    @PostMapping
    public Score addScore(@RequestBody Score score) {
        return service.saveScore(score);
    }

    /** Update — change the player name or score of an existing row. */
    @PutMapping("/{id}")
    public Score updateScore(@PathVariable Long id, @RequestBody Score score) {
        return service.updateScore(id, score);
    }

    /** Delete — remove a row. 404 if it doesn't exist. */
    @DeleteMapping("/{id}")
    public void deleteScore(@PathVariable Long id) {
        service.deleteScore(id);
    }

    // ---------- Business endpoints (backed by the service layer) ----------

    /** Top 10 scores across all players, descending. */
    @GetMapping("/leaderboard")
    public List<Score> getLeaderboard() {
        return service.getLeaderboard();
    }

    /** Aggregated stats (highest, average, total, games played) for one player. */
    @GetMapping("/player/{name}")
    public PlayerStatsDTO getPlayerStats(@PathVariable String name) {
        return service.getPlayerStats(name);
    }

    /** Full score history for a single player, sorted high-to-low. */
    @GetMapping("/player/{name}/history")
    public List<Score> getPlayerHistory(@PathVariable String name) {
        return service.getPlayerHistory(name);
    }

    /** Thematic SDG 14 summary of the community's collective impact. */
    @GetMapping("/impact")
    public ImpactDTO getEnvironmentalImpact() {
        return service.getEnvironmentalImpact();
    }
}
