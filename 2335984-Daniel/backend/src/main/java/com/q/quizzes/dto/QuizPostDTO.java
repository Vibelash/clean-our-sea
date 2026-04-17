package com.q.quizzes.dto;

public class QuizPostDTO {

    private String title;
    private String description;
    private String category;        //marine life, biodiversity etc
    private String difficulty;     //easy medium hard
    private int    totalQuestions;
    private Long   createdBy;

    public QuizPostDTO() {}

    public QuizPostDTO(String title, String description, String category,String difficulty, int totalQuestions, Long createdBy) 
    {
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.createdBy = createdBy;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }
    
    public String getTitle() 
    {
        return title;
    }

     public void setDescription(String description) 
    {
        this.description = description;
    }
    
    public String getDescription() 
    {
        return description;
    }   

    public void setCategory(String category) 
    {
       this.category = category;
    }
    
    public String getCategory() 
    {
        return category;
    }

    public void setDifficulty(String difficulty) 
    {
        this.difficulty = difficulty;
    }
    public String getDifficulty() 
    {
        return difficulty;
    }

    public void setTotalQuestions(int totalQuestions) 
    {
        this.totalQuestions = totalQuestions;
    }
    
    public int getTotalQuestions() 
    {
        return totalQuestions;
    }      

    public void setCreatedBy(Long createdBy) 
    {
        this.createdBy = createdBy;
    }

    public Long getCreatedBy() 
    {
        return createdBy;
    }


}
