/*
package com.example.seasweepers.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "reports")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double latitude;

    private double longitude;

    @Column(length = 500)
    private String note;

    private int stage;

    private String status;

    @ManyToOne
    @JoinColumn(name = "reported_by_user_id", nullable = false)
    private User reportedByUser;

    @ManyToOne
    @JoinColumn(name = "cleaned_by_user_id")
    private User cleanedByUser;

    @Column(name = "cleaned_at")
    private LocalDateTime cleanedAt;

    public Report() {}

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getReportedByUser() {
        return reportedByUser;
    }

    public void setReportedByUser(User reportedByUser) {
        this.reportedByUser = reportedByUser;
    }

    public User getCleanedByUser() {
        return cleanedByUser;
    }

    public void setCleanedByUser(User user) {
        this.cleanedByUser = user;
    }

    public LocalDateTime getCleanedAt() {
        return cleanedAt;
    }

    public void setCleanedAt(LocalDateTime cleanedAt) {
        this.cleanedAt = cleanedAt;
    }
}
*/
