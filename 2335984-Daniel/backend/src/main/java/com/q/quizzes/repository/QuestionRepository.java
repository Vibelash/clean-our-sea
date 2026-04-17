package com.q.quizzes.repository;

import com.q.quizzes.model.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface QuestionRepository extends CrudRepository<Question, Long> 
{
    List<Question> findByQuizId(Long quizId);
}
