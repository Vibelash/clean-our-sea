package com.q.quizzes.controller;

import com.q.quizzes.model.Question;
import com.q.quizzes.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController 
{
    @Autowired
    private QuestionService questionService;

    // GET /questions/quiz/1  →  returns all questions for that quiz
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<Question>> getByQuiz(@PathVariable Long quizId) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        // Return an empty list (200) so frontends can render a message rather than
        // receiving 404 which our static frontend treats as a load failure.
        return ResponseEntity.ok(questions);
    }
}
