package com.example.snakebackend.models;

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

    public Score() {}

    public Score(String player, int score) {
        this.player = player;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public String getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setScore(int score) {
        this.score = score;
    }
}