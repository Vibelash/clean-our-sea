package com.q.quizzes.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long quizId;

    @NotBlank
    private String title;

    @Column(length = 1000)
    private String description;

    private String category;

    //easy, medium, hard difficulty
    private String difficulty;
    private int totalQuestions;
    private Long createdBy;
    private Date createdDate;

    public Quiz() {}
    //  no id or set date values
    public Quiz(String title, String description, String category,
                String difficulty, int totalQuestions, Long createdBy, Date createdDate) 
    {
        this.title = title;
        this.description= description;
        this.category = category;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
    }

    public Long getQuizId()                      
    { 
        return quizId; 
    }
    public void setQuizId(Long quizId)           
    { 
        this.quizId = quizId; 
    }

    public String getTitle()                       
    { 
        
        return title; 
    }
    public void setTitle(String title)           
    { 
        this.title = title; 
    }

    public String getDescription()                 
    { 
        return description; 
    }
    public void setDescription(String d)         
    { 
        this.description = d; 
    }

    public String getCategory()                    
    { 
        return category; 
    }
    public void setCategory(String category)     
    { 
        this.category = category; 
    }

    public String getDifficulty()                  
    { 
        return difficulty; 
    }
    public void setDifficulty(String difficulty) 
    { 
        this.difficulty = difficulty; 
        
    }

    public int getTotalQuestions()                
    { 
        return totalQuestions; 
    }
    public void setTotalQuestions(int n)           
    { 
        this.totalQuestions = n; 
    }

    public Long getCreatedBy()                     
    { 
        return createdBy; 
    }
    public void setCreatedBy(Long createdBy)       
    { 
        this.createdBy = createdBy; 
    }

    public Date getCreatedDate()                   
    { 
        return createdDate; 
    }
    public void setCreatedDate(Date createdDate)   
    { 
        this.createdDate = createdDate; 
    }

    @Override
    public String toString() 
    {
        return "Quiz[id=" + quizId + ", title=" + title + ", difficulty=" + difficulty + "]";
    }
}
