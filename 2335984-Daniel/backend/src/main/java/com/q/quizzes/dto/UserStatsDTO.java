package com.q.quizzes.dto;

// sent back to the frontend as JSON when stats are requested
public class UserStatsDTO
{
    private int quizzesCompleted;
    private int averageScore;
    private int totalPoints;

    public UserStatsDTO(int quizzesCompleted, int averageScore, int totalPoints)
    {
        this.quizzesCompleted = quizzesCompleted;
        this.averageScore     = averageScore;
        this.totalPoints      = totalPoints;
    }

    public int getQuizzesCompleted() { return quizzesCompleted; }
    public int getAverageScore()     { return averageScore; }
    public int getTotalPoints()      { return totalPoints; }
}
