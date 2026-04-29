package com.example.snakebackend.dto;

/**
 * One row of GET /api/leaderboard — the cross-module leaderboard that
 * fuses every signal we have for a single user:
 *
 *   • snake game (com.example.snakebackend.Score)
 *   • quiz attempts (com.q.quizzes.UserQuiz)
 *   • the running totalScore on the User entity itself (Tala's module)
 *
 * The {@code combinedScore} field is what the rows are sorted by; it gives
 * each user one number that reflects their full participation across the
 * site, which is what the frontend hero leaderboard shows.
 */
public class UnifiedLeaderboardEntry {

    private Long userId;
    private String username;
    private int totalScore;       // Tala's running total (snake games via ScoreService)
    private int bestSnakeScore;   // single best Snake Infinity game
    private int bestQuizScore;    // single best UserQuiz attempt
    private int gamesPlayed;      // total snake games
    private int quizzesTaken;     // total UserQuiz rows
    private int combinedScore;    // totalScore + bestQuizScore — the sort key

    public UnifiedLeaderboardEntry() {}

    public UnifiedLeaderboardEntry(Long userId, String username,
                                   int totalScore, int bestSnakeScore,
                                   int bestQuizScore, int gamesPlayed,
                                   int quizzesTaken, int combinedScore) {
        this.userId = userId;
        this.username = username;
        this.totalScore = totalScore;
        this.bestSnakeScore = bestSnakeScore;
        this.bestQuizScore = bestQuizScore;
        this.gamesPlayed = gamesPlayed;
        this.quizzesTaken = quizzesTaken;
        this.combinedScore = combinedScore;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public int getBestSnakeScore() { return bestSnakeScore; }
    public void setBestSnakeScore(int bestSnakeScore) { this.bestSnakeScore = bestSnakeScore; }

    public int getBestQuizScore() { return bestQuizScore; }
    public void setBestQuizScore(int bestQuizScore) { this.bestQuizScore = bestQuizScore; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getQuizzesTaken() { return quizzesTaken; }
    public void setQuizzesTaken(int quizzesTaken) { this.quizzesTaken = quizzesTaken; }

    public int getCombinedScore() { return combinedScore; }
    public void setCombinedScore(int combinedScore) { this.combinedScore = combinedScore; }
}
