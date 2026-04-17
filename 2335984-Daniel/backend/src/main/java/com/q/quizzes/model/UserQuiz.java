package com.q.quizzes.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_quizzes")
public class UserQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long quizId;

    private int score;

    private Date dateTaken;

    public UserQuiz() {}

    public UserQuiz(Long userId, Long quizId, int score, Date dateTaken) 
    {
        this.userId= userId;
        this.quizId = quizId;
        this.score = score;
        this.dateTaken = dateTaken;
    }

    public Long getId()                      
    { 
        return id; 
    }
    public void setId(Long id)               
    { 
        this.id = id; 
    }

    public Long getUserId()                  
    { 
        return userId; 
    }
    public void setUserId(Long userId)       
    { 
        this.userId = userId;
    }

    public Long getQuizId()                  
    { 
        return quizId;
    }
    public void setQuizId(Long quizId)       
    { 
        this.quizId = quizId; 
    }

    public int  getScore()                   
    { 
        return score; 
    }
    public void setScore(int score)          
    { 
        this.score = score; 
    }

    public Date getDateTaken()               
    { 
        return dateTaken; 
    }
    public void setDateTaken(Date dateTaken) 
    { 
        this.dateTaken = dateTaken; 
    }

    @Override
    public String toString() 
    {
        return "UserQuiz[userId=" + userId + ", quizId=" + quizId + ", score=" + score + "]";
    }
}
