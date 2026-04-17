package com.example.seasweepers.Models;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 500)
    private String bio;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "country")
private String country;
    

    private int followers;


    @Column(name = "total_score")
    private int totalScore;

    @Column(name = "weekly_goal")
    private int weeklyGoal;

    @Column(name = "weekly_points")
    private int weeklyPoints;

    public User() {}

    public User(String username,
                String bio,
                String profilePicture,
                String country,
                int followers,
                int totalScore,
                int weeklyGoal,
                int weeklyPoints) {
        this.username = username;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.country = country;
        this.followers = followers;
        this.totalScore = totalScore;
        this.weeklyGoal = weeklyGoal;
        this.weeklyPoints = weeklyPoints;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getCountry() {
    return country;
}

public void setCountry(String country) {
    this.country = country;
}

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(int weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public int getWeeklyPoints() {
        return weeklyPoints;
    }

    public void setWeeklyPoints(int weeklyPoints) {
        this.weeklyPoints = weeklyPoints;
    }
}
