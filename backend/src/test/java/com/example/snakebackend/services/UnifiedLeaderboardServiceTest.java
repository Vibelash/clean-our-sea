package com.example.snakebackend.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.cos.combined.CleanOurSeaApplication;
import com.example.seasweepers.Models.User;
import com.example.seasweepers.Repos.UserRepository;
import com.example.snakebackend.dto.UnifiedLeaderboardEntry;
import com.example.snakebackend.models.Score;
import com.example.snakebackend.repos.ScoreRepo;
import com.q.quizzes.model.UserQuiz;
import com.q.quizzes.repository.UserQuizRepository;

/**
 * Verifies that the cross-module leaderboard correctly joins data from
 * three teammates' tables: users (Tala), score (Karan) and user_quizzes
 * (Daniel).
 */
@SpringBootTest(classes = CleanOurSeaApplication.class)
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UnifiedLeaderboardServiceTest {

    @Autowired private UnifiedLeaderboardService leaderboard;
    @Autowired private UserRepository users;
    @Autowired private ScoreRepo scores;
    @Autowired private UserQuizRepository quizzes;

    @BeforeEach
    void wipe() {
        scores.deleteAll();
        quizzes.deleteAll();
        users.deleteAll();
    }

    @Test
    @DisplayName("ranks by totalScore + bestQuizScore and pulls best snake game in")
    void unifiedRanking_combinesAllThreeModules() {
        User a = users.save(makeUser("alice", 200));
        User b = users.save(makeUser("bob",   100));
        User c = users.save(makeUser("carol",   0));

        // alice: 1 snake game @ 50, 2 quizzes (best 80)  -> combined = 200+80 = 280
        Score s1 = new Score("alice", 50); s1.setUserId(a.getId()); scores.save(s1);
        quizzes.save(new UserQuiz(a.getId(), 1L, 60, new Date()));
        quizzes.save(new UserQuiz(a.getId(), 2L, 80, new Date()));

        // bob: 2 snake games (best 90), 1 quiz @ 40       -> combined = 100+40 = 140
        Score s2 = new Score("bob", 60); s2.setUserId(b.getId()); scores.save(s2);
        Score s3 = new Score("bob", 90); s3.setUserId(b.getId()); scores.save(s3);
        quizzes.save(new UserQuiz(b.getId(), 1L, 40, new Date()));

        // carol: nothing — should be filtered out

        List<UnifiedLeaderboardEntry> board = leaderboard.getUnifiedLeaderboard();

        assertEquals(2, board.size(), "carol with 0 activity should be filtered");
        UnifiedLeaderboardEntry top = board.get(0);
        assertEquals("alice", top.getUsername());
        assertEquals(200, top.getTotalScore());
        assertEquals(50,  top.getBestSnakeScore());
        assertEquals(80,  top.getBestQuizScore());
        assertEquals(2,   top.getQuizzesTaken());
        assertEquals(280, top.getCombinedScore());

        UnifiedLeaderboardEntry second = board.get(1);
        assertEquals("bob", second.getUsername());
        assertEquals(90, second.getBestSnakeScore(), "best of bob's two games");
        assertEquals(2,  second.getGamesPlayed());
        assertEquals(140, second.getCombinedScore());

        // Carol must not appear — proves the empty-row filter works.
        assertTrue(board.stream().noneMatch(e -> "carol".equals(e.getUsername())));
    }

    private static User makeUser(String username, int totalScore) {
        return new User(username, "bio", null, "UK", 0, totalScore, 0, 0);
    }
}
