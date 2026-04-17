package com.example.snakebackend.dto;

/**
 * Summary statistics for a single player, computed in the service layer
 * from their full score history. Returned by GET /scores/player/{name}.
 */
public class PlayerStatsDTO {

    private String player;
    private int highestScore;
    private double averageScore;
    private long totalScore;
    private int gamesPlayed;

    public PlayerStatsDTO() {}

    public PlayerStatsDTO(String player, int highestScore, double averageScore,
                          long totalScore, int gamesPlayed) {
        this.player = player;
        this.highestScore = highestScore;
        this.averageScore = averageScore;
        this.totalScore = totalScore;
        this.gamesPlayed = gamesPlayed;
    }

    public String getPlayer() { return player; }
    public int getHighestScore() { return highestScore; }
    public double getAverageScore() { return averageScore; }
    public long getTotalScore() { return totalScore; }
    public int getGamesPlayed() { return gamesPlayed; }

    public void setPlayer(String player) { this.player = player; }
    public void setHighestScore(int highestScore) { this.highestScore = highestScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }
    public void setTotalScore(long totalScore) { this.totalScore = totalScore; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
}
