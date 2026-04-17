package com.example.backend.controllers;

import com.example.backend.models.PollutionReport;
import com.example.backend.service.PollutionReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin // allows frontend to connect
public class PollutionReportController {

    @Autowired
    private PollutionReportService reportService;

    // CREATE
    @PostMapping
    public PollutionReport createReport(@RequestBody PollutionReport report) {

        // 🔥 ALWAYS set time in backend
        report.setCreatedAt(java.time.LocalDateTime.now().toString());

        // 🔥 Ensure user exists
        if (report.getUserId() == null || report.getUserId().isEmpty()) {
            report.setUserId("DemoUser");
        }

        return reportService.createReport(report);
    }

    // READ
    @GetMapping
    public List<PollutionReport> getReports() {
        return reportService.getAllReports();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }
}