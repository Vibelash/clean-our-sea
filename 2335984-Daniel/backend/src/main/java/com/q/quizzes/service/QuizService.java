package com.q.quizzes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.q.quizzes.exception.ResourceNotFoundException;
import com.q.quizzes.model.Quiz;
import com.q.quizzes.repository.QuizRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public List<Quiz> getAllQuizzes() {
        return (List<Quiz>) quizRepository.findAll();
    }

    public Optional<Quiz> getQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public List<Quiz> getByDifficulty(String difficulty) {
        return quizRepository.findByDifficulty(difficulty);
    }

    public List<Quiz> getByCategory(String category) {
        return quizRepository.findByCategory(category);
    }

    public Quiz addQuiz(Quiz quiz) {
        quiz.setCreatedDate(new Date()); //timestamp always comes from the server
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        quizRepository.delete(quiz);
    }

    public Quiz updateQuiz(Long id, Quiz updated) 
    {
        //fetch existing record first and only update specigic fields like title
        Quiz existing = quizRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setDifficulty(updated.getDifficulty());
        existing.setTotalQuestions(updated.getTotalQuestions());
        return quizRepository.save(existing);
    }
}
