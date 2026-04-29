package com.example.snakebackend.services;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.seasweepers.Models.User;
import com.example.seasweepers.Repos.UserRepository;
import com.example.snakebackend.dto.ImpactDTO;
import com.example.snakebackend.dto.PlayerStatsDTO;
import com.example.snakebackend.models.Score;
import com.example.snakebackend.repos.ScoreRepo;

/**
 * Business logic layer for the Snake Infinity scoreboard.
 *
 * The controller should never touch the repository directly — it routes HTTP
 * requests and delegates here. This class is where validation, aggregation,
 * and the thematic SDG 14 computation live.
 */
@Service
public class ScoreService {

    /** Every 10 in-game points is treated as one simulated piece of plastic cleaned. */
    private static final int POINTS_PER_PIECE_OF_PLASTIC = 10;

    /** Max rows returned by the global leaderboard. */
    private static final int LEADERBOARD_SIZE = 10;

    private final ScoreRepo repo;
    private final UserRepository userRepository;

    public ScoreService(ScoreRepo repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    // ---------- Read ----------

    public List<Score> getAllScores() {
        return repo.findAll();
    }

    public Score getScoreById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Score " + id + " not found"));
    }

    // ---------- Create ----------

    /**
     * Persist a new score after validating it. Business rules:
     *   - player name must not be null/blank
     *   - score must be non-negative
     * Violations produce a 400 Bad Request.
     */
    public Score saveScore(Score score) {
        if (score == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score payload is missing");
        }
        if (score.getPlayer() == null || score.getPlayer().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player name is required");
        }
        if (score.getScore() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score cannot be negative");
        }
        // Trim whitespace so "Alice " and "Alice" are treated as the same player.
        score.setPlayer(score.getPlayer().trim());
        Score saved = repo.save(score);

        // Cross-module link: bump the matching User's totalScore + weeklyPoints
        // so Tala's /leaderboard reflects this game. Silently no-ops if the
        // game was anonymous (no userId) or the user no longer exists.
        Long uid = saved.getUserId();
        if (uid != null) {
            userRepository.findById(uid).ifPresent(u -> {
                u.setTotalScore(u.getTotalScore() + saved.getScore());
                u.setWeeklyPoints(u.getWeeklyPoints() + saved.getScore());
                userRepository.save(u);
            });
        }
        return saved;
    }

    // ---------- Update ----------

    public Score updateScore(Long id, Score updated) {
        Score existing = getScoreById(id);
        if (updated.getPlayer() != null && !updated.getPlayer().isBlank()) {
            existing.setPlayer(updated.getPlayer().trim());
        }
        if (updated.getScore() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Score cannot be negative");
        }
        existing.setScore(updated.getScore());
        return repo.save(existing);
    }

    public Score renamePlayer(Long id, String newPlayer) {
        if (newPlayer == null || newPlayer.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player name is required");
        }
        Score existing = getScoreById(id);
        existing.setPlayer(newPlayer.trim());
        return repo.save(existing);
    }

    // ---------- Delete ----------

    public void deleteScore(Long id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Score " + id + " not found");
        }
        repo.deleteById(id);
    }

    // ---------- Business logic / derived data ----------

    /**
     * Global leaderboard — top 10 distinct players, each represented by
     * their single best score.
     *
     * Business logic: group every score by player, pick each player's
     * personal best, sort descending, and take the top 10. This is done
     * with streams in the service layer so the controller and repository
     * stay clean.
     */
    public List<Score> getLeaderboard() {
        return repo.findAll().stream()
                // Normalise the key so "Karan", "karan", and "Karan " all collapse
                // into the same bucket — no duplicate names on the leaderboard.
                .collect(Collectors.groupingBy(
                        s -> s.getPlayer() == null ? "" : s.getPlayer().trim().toLowerCase(),
                        Collectors.maxBy(Comparator.comparingInt(Score::getScore))))
                .values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .sorted(Comparator.comparingInt(Score::getScore).reversed())
                .limit(LEADERBOARD_SIZE)
                .toList();
    }

    /**
     * Personal score history for a single player — every game they've
     * played, sorted by score descending. Used by the "click a player"
     * detail view in the frontend.
     */
    public List<Score> getPlayerHistory(String player) {
        if (player == null || player.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player name is required");
        }
        return repo.findByPlayer(player.trim()).stream()
                .sorted(Comparator.comparingInt(Score::getScore).reversed())
                .toList();
    }

    /**
     * Aggregate all of a player's games into a single summary.
     * Throws 404 if the player has never played.
     */
    public PlayerStatsDTO getPlayerStats(String player) {
        if (player == null || player.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Player name is required");
        }
        List<Score> scores = repo.findByPlayer(player.trim());
        if (scores.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No scores found for player " + player);
        }

        int highest = scores.stream().mapToInt(Score::getScore).max().orElse(0);
        long total = scores.stream().mapToLong(Score::getScore).sum();
        double average = (double) total / scores.size();
        int gamesPlayed = scores.size();

        return new PlayerStatsDTO(player.trim(), highest, average, total, gamesPlayed);
    }

    /**
     * Thematic SDG 14 summary: converts the community's total score into a
     * notional "pieces of plastic cleaned" figure. This is the method that
     * ties the backend back to the Life Below Water problem statement.
     */
    public ImpactDTO getEnvironmentalImpact() {
        List<Score> all = repo.findAll();
        long totalScore = all.stream().mapToLong(Score::getScore).sum();
        long pieces = totalScore / POINTS_PER_PIECE_OF_PLASTIC;
        long players = all.stream().map(Score::getPlayer).distinct().count();
        long games = all.size();

        String message = String.format(
                "The Snake Infinity community has virtually cleaned %d pieces of plastic " +
                "across %d games played by %d players.",
                pieces, games, players);

        return new ImpactDTO(totalScore, pieces, players, games, message);
    }
}
