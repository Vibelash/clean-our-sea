package com.example.snakebackend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String player;

    private int score;

    /**
     * FK to com.example.seasweepers.Models.User#id. Optional so anonymous
     * games still save (legacy snakebackend deployments allowed null).
     * When set, ScoreService bumps the user's totalScore so Tala's
     * /leaderboard reflects this game.
     */
    @Column(name = "user_id")
    private Long userId;

    public Score() {}

    public Score(String player, int score) {
        this.player = player;
        this.score = score;
    }

    public Long getId() { return id; }
    public String getPlayer() { return player; }
    public int getScore() { return score; }
    public Long getUserId() { return userId; }

    public void setPlayer(String player) { this.player = player; }
    public void setScore(int score) { this.score = score; }
    public void setUserId(Long userId) { this.userId = userId; }
}
