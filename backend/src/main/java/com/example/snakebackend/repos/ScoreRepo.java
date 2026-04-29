package com.example.snakebackend.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.snakebackend.models.Score;

public interface ScoreRepo extends JpaRepository<Score, Long> {

    // Derived queries — Spring Data JPA implements these from the method name.
    List<Score> findByPlayer(String player);

    List<Score> findTop10ByOrderByScoreDesc();

    /** All games played by one authenticated user. Used by the unified leaderboard. */
    List<Score> findByUserId(Long userId);
}
