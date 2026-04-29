package com.example.snakebackend.services;

import static org.junit.jupiter.api.Assertions.*;

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
import com.example.snakebackend.dto.ImpactDTO;
import com.example.snakebackend.dto.PlayerStatsDTO;
import com.example.snakebackend.models.Score;
import com.example.snakebackend.repos.ScoreRepo;

/**
 * Integration tests for the snake scoreboard logic. Same setup pattern
 * as {@link NewsServiceTest}: in-memory H2 via the test profile, every
 * method transactional + rolled back.
 *
 * Focus is on the non-trivial parts of the service — leaderboard
 * de-duplication, player stats, and the SDG 14 impact calculation.
 */
@SpringBootTest(classes = CleanOurSeaApplication.class)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ScoreServiceTest {

    @Autowired private ScoreService scores;
    @Autowired private ScoreRepo repo;

    @BeforeEach
    void wipe() {
        repo.deleteAll();
    }

    @Test
    @DisplayName("saveScore rejects blank player name with 400")
    void save_blankPlayer_throws() {
        Score bad = new Score(" ", 100);
        assertThrows(ResponseStatusException.class, () -> scores.saveScore(bad));
    }

    @Test
    @DisplayName("saveScore rejects negative score with 400")
    void save_negative_throws() {
        Score bad = new Score("Karan", -1);
        assertThrows(ResponseStatusException.class, () -> scores.saveScore(bad));
    }

    @Test
    @DisplayName("saveScore trims whitespace on player name")
    void save_trims() {
        Score saved = scores.saveScore(new Score("  Karan  ", 50));
        assertEquals("Karan", saved.getPlayer());
    }

    @Test
    @DisplayName("getLeaderboard collapses duplicate names and shows personal bests")
    void leaderboard_dedupes() {
        scores.saveScore(new Score("Karan", 30));
        scores.saveScore(new Score("Karan", 95));     // personal best for Karan
        scores.saveScore(new Score("karan", 10));     // case-insensitive collision
        scores.saveScore(new Score("Tarun", 40));
        scores.saveScore(new Score("Tala", 88));

        List<Score> top = scores.getLeaderboard();
        assertEquals(3, top.size(), "three distinct players");
        assertEquals(95, top.get(0).getScore(), "Karan's best is on top");
        assertEquals(88, top.get(1).getScore());
        assertEquals(40, top.get(2).getScore());
    }

    @Test
    @DisplayName("getPlayerStats reports highest, average, total and games played")
    void playerStats_aggregates() {
        scores.saveScore(new Score("Tala", 30));
        scores.saveScore(new Score("Tala", 60));
        scores.saveScore(new Score("Tala", 90));

        PlayerStatsDTO s = scores.getPlayerStats("Tala");
        assertEquals(90, s.getHighestScore());
        assertEquals(180, s.getTotalScore());
        assertEquals(60.0, s.getAverageScore(), 0.001);
        assertEquals(3, s.getGamesPlayed());
    }

    @Test
    @DisplayName("getPlayerStats throws 404 when player has never played")
    void playerStats_unknown_404() {
        assertThrows(ResponseStatusException.class,
                () -> scores.getPlayerStats("Nobody"));
    }

    @Test
    @DisplayName("getEnvironmentalImpact converts total score into pieces of plastic")
    void impact_convertsTotalScore() {
        scores.saveScore(new Score("Karan", 100));
        scores.saveScore(new Score("Tala", 250));
        // total 350 in-game points / 10 = 35 pieces of plastic cleaned.

        ImpactDTO impact = scores.getEnvironmentalImpact();
        assertEquals(350L, impact.getTotalScore());
        assertEquals(35L, impact.getPiecesOfPlasticCleaned());
        assertEquals(2L, impact.getTotalPlayers());
        assertEquals(2L, impact.getTotalGamesPlayed());
        assertNotNull(impact.getMessage());
    }

    @Test
    @DisplayName("updateScore changes player and score; deleteScore removes it")
    void update_then_delete() {
        Score s = scores.saveScore(new Score("Karan", 40));
        Score updated = scores.updateScore(s.getId(), new Score("Karan-updated", 75));
        assertEquals("Karan-updated", updated.getPlayer());
        assertEquals(75, updated.getScore());

        scores.deleteScore(s.getId());
        assertThrows(ResponseStatusException.class,
                () -> scores.getScoreById(s.getId()));
    }
}
