package com.q.quizzes.repository;

import org.springframework.data.repository.CrudRepository;

import com.q.quizzes.model.UserQuiz;

import java.util.List;

public interface UserQuizRepository extends CrudRepository<UserQuiz, Long> 
{

    List<UserQuiz> findByUserId(Long userId);

    List<UserQuiz> findByQuizId(Long quizId);

    boolean existsByUserIdAndQuizId(Long userId, Long quizId);
}
