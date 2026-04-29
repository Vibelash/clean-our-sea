package com.example.seasweepers.Services;

import com.example.seasweepers.Models.Report;
import com.example.seasweepers.Models.User;
import com.example.seasweepers.Repos.ReportRepository;
import com.example.seasweepers.Repos.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public Report createReport(double latitude,
                               double longitude,
                               String note,
                               int stage,
                               Long reportedByUserId) {
        if (stage < 1 || stage > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stage must be between 1 and 5");
        }

        User reporter = userRepository.findById(reportedByUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reporting user not found"));

        Report report = new Report();
        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setNote(note);
        report.setStage(stage);
        report.setStatus("reported");
        report.setReportedByUser(reporter);

        return reportRepository.save(report);
    }

    public List<Report> getReports(String status) {
        if (status == null || status.isBlank()) {
            return reportRepository.findAll();
        }
        return reportRepository.findByStatusIgnoreCase(status);
    }

    @Transactional
    public Report confirmCleanup(Long reportId, Long userId) {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report not found"));

        if ("cleaned".equalsIgnoreCase(report.getStatus()) || report.getCleanedByUser() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report already cleaned");
        }

        User cleaner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cleaning user not found"));

        int points = calculatePoints(report.getStage());

        cleaner.setTotalScore(cleaner.getTotalScore() + points);
        cleaner.setWeeklyPoints(cleaner.getWeeklyPoints() + points);

        report.setStatus("cleaned");
        report.setCleanedByUser(cleaner);
        report.setCleanedAt(LocalDateTime.now());

        userRepository.save(cleaner);

        return reportRepository.save(report);
    }

    private int calculatePoints(int stage){

        switch(stage){
            case 1: return 10;
            case 2: return 20;
            case 3: return 30;
            case 4: return 40;
            case 5: return 50;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stage value");
        }
    }
}
