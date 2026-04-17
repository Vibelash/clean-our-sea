package com.example.snakebackend.dto;

/**
 * Thematic summary tying the game back to UN SDG 14 (Life Below Water).
 * Converts the total in-game score into a notional "pieces of plastic cleaned"
 * figure so the community can see their collective impact.
 * Returned by GET /scores/impact.
 */
public class ImpactDTO {

    private long totalScore;
    private long piecesOfPlasticCleaned;
    private long totalPlayers;
    private long totalGamesPlayed;
    private String message;

    public ImpactDTO() {}

    public ImpactDTO(long totalScore, long piecesOfPlasticCleaned,
                     long totalPlayers, long totalGamesPlayed, String message) {
        this.totalScore = totalScore;
        this.piecesOfPlasticCleaned = piecesOfPlasticCleaned;
        this.totalPlayers = totalPlayers;
        this.totalGamesPlayed = totalGamesPlayed;
        this.message = message;
    }

    public long getTotalScore() { return totalScore; }
    public long getPiecesOfPlasticCleaned() { return piecesOfPlasticCleaned; }
    public long getTotalPlayers() { return totalPlayers; }
    public long getTotalGamesPlayed() { return totalGamesPlayed; }
    public String getMessage() { return message; }

    public void setTotalScore(long totalScore) { this.totalScore = totalScore; }
    public void setPiecesOfPlasticCleaned(long p) { this.piecesOfPlasticCleaned = p; }
    public void setTotalPlayers(long totalPlayers) { this.totalPlayers = totalPlayers; }
    public void setTotalGamesPlayed(long g) { this.totalGamesPlayed = g; }
    public void setMessage(String message) { this.message = message; }
}
