package com.example.seasweepers.Controllers;

import com.example.seasweepers.Models.Report;
import com.example.seasweepers.Services.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public List<Report> getReports(@RequestParam(required = false) String status) {
        return reportService.getReports(status);
    }

    @PostMapping
    public Report createReport(@RequestBody CreateReportRequest request) {
        return reportService.createReport(
                request.getLatitude(),
                request.getLongitude(),
                request.getNote(),
                request.getStage(),
                request.getReportedByUserId()
        );
    }

    @PostMapping("/{reportId}/clean/{userId}")
    public Report confirmCleanup(@PathVariable Long reportId,
                                 @PathVariable Long userId){

        return reportService.confirmCleanup(reportId,userId);
    }

    public static class CreateReportRequest {
        private double latitude;
        private double longitude;
        private String note;
        private int stage;
        private Long reportedByUserId;

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

        public Long getReportedByUserId() {
            return reportedByUserId;
        }

        public void setReportedByUserId(Long reportedByUserId) {
            this.reportedByUserId = reportedByUserId;
        }
    }
}
*/
