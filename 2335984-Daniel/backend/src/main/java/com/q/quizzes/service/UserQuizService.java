package com.q.quizzes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.q.quizzes.dto.UserStatsDTO;
import com.q.quizzes.exception.ResourceNotFoundException;
import com.q.quizzes.model.UserQuiz;
import com.q.quizzes.repository.UserQuizRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserQuizService 
{

    @Autowired
    private UserQuizRepository userQuizRepository;

    public List<UserQuiz> getAllUserQuizzes() 
    {
        return (List<UserQuiz>) userQuizRepository.findAll();
    }

    public Optional<UserQuiz> getById(Long id) 
    {
        return userQuizRepository.findById(id);
    }

    public List<UserQuiz> getAttemptsByUser(Long userId) 
    {
        return userQuizRepository.findByUserId(userId);
    }

    public List<UserQuiz> getAttemptsByQuiz(Long quizId) 
    {
        return userQuizRepository.findByQuizId(quizId);
    }

    public UserQuiz submitAttempt(UserQuiz attempt) 
    {
        //stamps the date before saving
        attempt.setDateTaken(new Date());
        return userQuizRepository.save(attempt);
    }

    public void deleteAttempt(Long id)
    {
        //checks if exists
        UserQuiz attempt = userQuizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserQuiz", "id", id));
        userQuizRepository.delete(attempt);
    }

    // calculates live stats for a specific user from their attempt history
    public UserStatsDTO getStatsForUser(Long userId)
    {
        List<UserQuiz> attempts = userQuizRepository.findByUserId(userId);

        int quizzesCompleted = attempts.size();

        // return zeros if the user has not attempted anything yet
        if (quizzesCompleted == 0)
        {
            return new UserStatsDTO(0, 0, 0);
        }

        int totalPoints = 0;
        for (UserQuiz a : attempts)
        {
            totalPoints += a.getScore();
        }

        int averageScore = totalPoints / quizzesCompleted;

        return new UserStatsDTO(quizzesCompleted, averageScore, totalPoints);
    }
}
