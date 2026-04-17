package com.cos.combined;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Single entry point for the combined backend.
 *
 * The 5 teammate modules (Daniel / Tarun / May / Tala / Karan) each shipped
 * their own Spring Boot main class. In this "clean-our-sea-local" copy we
 * boot them together out of one JAR, on one port (8080), against one H2
 * database — so the whole site runs from a single process, no Firebase, no
 * Render.
 *
 * We need three separate scans because the 5 modules live in 5 distinct
 * root packages with no common parent:
 *   com.q.quizzes             (Daniel)
 *   communities.communities   (Tarun)
 *   com.example.backend       (May)
 *   com.example.seasweepers   (Tala)
 *   com.example.snakebackend  (Karan)
 *   com.cos.combined          (this module — CORS + future glue)
 */
@SpringBootApplication(scanBasePackages = {
        "com.q.quizzes",
        "communities.communities",
        "com.example.backend",
        "com.example.seasweepers",
        "com.example.snakebackend",
        "com.cos.combined"
})
@EntityScan(basePackages = {
        "com.q.quizzes.model",
        "communities.communities.model",
        "com.example.backend.models",
        "com.example.seasweepers.Models",
        "com.example.snakebackend.models"
})
@EnableJpaRepositories(basePackages = {
        "com.q.quizzes.repository",
        "communities.communities.repository",
        "com.example.backend.repositories",
        "com.example.seasweepers.Repos",
        "com.example.snakebackend.repos"
})
@EnableJpaAuditing  // Tala's module relied on this; harmless for the others
public class CleanOurSeaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CleanOurSeaApplication.class, args);
    }
}
