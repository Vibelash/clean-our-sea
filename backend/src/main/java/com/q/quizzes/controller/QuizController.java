package com.q.quizzes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.q.quizzes.dto.QuizPostDTO;
import com.q.quizzes.model.Quiz;
import com.q.quizzes.service.QuizService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/quiz")
public class QuizController 
{

    @Autowired
    private QuizService quizService;
// GET /quiz
    
    @GetMapping
    public List<Quiz> getAllQuizzes() 
    {
        return quizService.getAllQuizzes();
    }

    
// GET /quiz/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getById(@PathVariable Long id) 
    {
        Optional<Quiz> quiz = quizService.getQuizById(id);
        return quiz.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    // GET /quiz/difficulty/Easy
    @GetMapping("/difficulty/{level}")
    public List<Quiz> getByDifficulty(@PathVariable String level) 
    {
        return quizService.getByDifficulty(level);
    }

    //GET /quiz/category/Marine Life
    @GetMapping("/category/{name}")
    public List<Quiz> getByCategory(@PathVariable String name) 
    {
        return quizService.getByCategory(name);
    }

    // POST/quiz
    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody QuizPostDTO dto) 
    {
        //checks if title isnt blank and questions arent empty
        if (dto.getTitle() == null || dto.getTitle().isBlank()
                || dto.getDifficulty() == null
                || dto.getTotalQuestions() <= 0) 
                {
            return ResponseEntity.badRequest().build(); //error handling
        }
        Quiz quiz = new Quiz(
                dto.getTitle(), dto.getDescription(), dto.getCategory(),
                dto.getDifficulty(), dto.getTotalQuestions(), dto.getCreatedBy(), null);
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.addQuiz(quiz));
    }

    // PUT /quiz/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody QuizPostDTO dto) 
    {
        Quiz updated = new Quiz(
                dto.getTitle(), dto.getDescription(), dto.getCategory(),
                dto.getDifficulty(), dto.getTotalQuestions(), dto.getCreatedBy(), null);
        return ResponseEntity.ok(quizService.updateQuiz(id, updated));
    }

    // DELETE /quiz/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuiz(@PathVariable Long id) 
    {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok("Quiz deleted successfully.");
    }
}
