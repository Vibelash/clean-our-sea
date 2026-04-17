package com.q.quizzes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.q.quizzes.dto.UserQuizPostDTO;
import com.q.quizzes.model.UserQuiz;
import com.q.quizzes.service.UserQuizService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user-quiz")
public class UserQuizController 
{

    @Autowired
    private UserQuizService userQuizService;

    //GET /user-quiz
    @GetMapping
    public List<UserQuiz> getAll() 
    {
        return userQuizService.getAllUserQuizzes();
    }

    // GET /user-quiz/[id]
    @GetMapping("/{id}")
    public ResponseEntity<UserQuiz> getById(@PathVariable Long id) 
    {
        Optional<UserQuiz> uq = userQuizService.getById(id);
        return uq.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }

    // GET /user-quiz/user/[userid]
    @GetMapping("/user/{userId}")
    public List<UserQuiz> getByUser(@PathVariable Long userId) 
    {
        return userQuizService.getAttemptsByUser(userId);
    }

    // GET /user-quiz/quiz/[quizid]
    @GetMapping("/quiz/{quizId}")
    public List<UserQuiz> getByQuiz(@PathVariable Long quizId) 
    {
        return userQuizService.getAttemptsByQuiz(quizId);
    }

    // POST /user-quiz
    @PostMapping
    public ResponseEntity<UserQuiz> submitAttempt(@RequestBody UserQuizPostDTO dto) 
    {
        if (dto.getUserId() == null || dto.getQuizId() == null) 
            {
            return ResponseEntity.badRequest().build();
            }
        UserQuiz attempt = new UserQuiz(dto.getUserId(), dto.getQuizId(), dto.getScore(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(userQuizService.submitAttempt(attempt));
    }

    // DELETE /user-quiz/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAttempt(@PathVariable Long id) 
    {
        userQuizService.deleteAttempt(id);
        return ResponseEntity.ok("Quiz attempt deleted.");
    }
}
