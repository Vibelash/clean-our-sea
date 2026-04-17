package com.example.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class PollutionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;
    private double longitude;

    private String description;
    private int stage;

    private String userId;
    private String createdAt;

    public PollutionReport() {}

    // GETTERS
    public Long getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getDescription() { return description; }
    public int getStage() { return stage; }
    public String getUserId() { return userId; }
    public String getCreatedAt() { return createdAt; }

    // SETTERS
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setDescription(String description) { this.description = description; }
    public void setStage(int stage) { this.stage = stage; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}