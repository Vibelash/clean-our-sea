package com.q.quizzes.service;

import com.q.quizzes.model.Question;
import com.q.quizzes.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService 
{
    @Autowired
    private QuestionRepository questionRepository;

    public List<Question> getQuestionsByQuizId(Long quizId) 
    {
        return questionRepository.findByQuizId(quizId);
    }
}
