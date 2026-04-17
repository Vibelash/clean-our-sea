package com.example.backend.controllers;

import com.example.backend.models.PollutionReport;
import com.example.backend.service.PollutionReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pollution-reports")
@CrossOrigin // allows frontend to connect
// NOTE (combined backend): path was "/reports" in May's standalone app.
// Renamed to "/pollution-reports" here to avoid colliding with Tala's
// ReportController which also owns "/reports" (different request/response shape).
public class PollutionReportController {

    @Autowired
    private PollutionReportService reportService;

    // CREATE
    @PostMapping
    public PollutionReport createReport(@RequestBody PollutionReport report) {
        return reportService.createReport(report);
    }

    // READ
    @GetMapping
    public List<PollutionReport> getReports() {
        return reportService.getAllReports();
    }

    // DELETE (for Clean Up button)
    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }
}