package com.q.quizzes.repository;

import org.springframework.data.repository.CrudRepository;

import com.q.quizzes.model.Quiz;

import java.util.List;

public interface QuizRepository extends CrudRepository<Quiz, Long> 
{

    List<Quiz> findByDifficulty(String difficulty);

    List<Quiz> findByCategory(String category);

    List<Quiz> findByCreatedBy(Long createdBy);
}
