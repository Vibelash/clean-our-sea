package com.example.snakebackend.services;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.seasweepers.Models.User;
import com.example.seasweepers.Repos.UserRepository;
import com.example.snakebackend.dto.UnifiedLeaderboardEntry;
import com.example.snakebackend.models.Score;
import com.example.snakebackend.repos.ScoreRepo;
import com.q.quizzes.model.UserQuiz;
import com.q.quizzes.repository.UserQuizRepository;

/**
 * Cross-module leaderboard. Reads from three repositories owned by three
 * different teammates — Tala's UserRepository, Karan's ScoreRepo, and
 * Daniel's UserQuizRepository — and joins them in memory into a single
 * ranked list.
 *
 * The reason this lives in the snakebackend package is integration: the
 * snake module is the natural fan-in point because every gameplay event
 * already passes through here, and adding the unified view here avoids
 * touching teammates' code.
 */
@Service
public class UnifiedLeaderboardService {

    /** Hard cap so a giant user table doesn't blow up the response payload. */
    private static final int LEADERBOARD_LIMIT = 25;

    private final UserRepository users;
    private final ScoreRepo scores;
    private final UserQuizRepository quizzes;

    public UnifiedLeaderboardService(UserRepository users,
                                     ScoreRepo scores,
                                     UserQuizRepository quizzes) {
        this.users = users;
        this.scores = scores;
        this.quizzes = quizzes;
    }

    /**
     * Returns the top {@value #LEADERBOARD_LIMIT} users by combined score
     * (running totalScore + best quiz score). Users with zero activity
     * across all three modules are dropped so the demo only shows real
     * participants.
     */
    public List<UnifiedLeaderboardEntry> getUnifiedLeaderboard() {
        return users.findAll().stream()
                .map(this::buildEntry)
                .filter(e -> e.getCombinedScore() > 0
                          || e.getGamesPlayed() > 0
                          || e.getQuizzesTaken() > 0)
                .sorted(Comparator
                        .comparingInt(UnifiedLeaderboardEntry::getCombinedScore).reversed()
                        .thenComparing(Comparator
                                .comparingInt(UnifiedLeaderboardEntry::getBestSnakeScore).reversed()))
                .limit(LEADERBOARD_LIMIT)
                .toList();
    }

    private UnifiedLeaderboardEntry buildEntry(User u) {
        List<Score> userScores = scores.findByUserId(u.getId());
        int bestSnake = userScores.stream().mapToInt(Score::getScore).max().orElse(0);
        int games = userScores.size();

        List<UserQuiz> userQuizzes = quizzes.findByUserId(u.getId());
        int bestQuiz = userQuizzes.stream().mapToInt(UserQuiz::getScore).max().orElse(0);
        int quizCount = userQuizzes.size();

        int combined = u.getTotalScore() + bestQuiz;

        return new UnifiedLeaderboardEntry(
                u.getId(),
                u.getUsername(),
                u.getTotalScore(),
                bestSnake,
                bestQuiz,
                games,
                quizCount,
                combined);
    }
}
