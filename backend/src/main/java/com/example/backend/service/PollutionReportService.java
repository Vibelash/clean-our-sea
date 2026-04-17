package com.example.backend.service;

import com.example.backend.models.PollutionReport;
import com.example.backend.repositories.PollutionReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PollutionReportService {

    @Autowired
    private PollutionReportRepository reportRepository;

    // CREATE - save new report
    public PollutionReport createReport(PollutionReport report) {
        return reportRepository.save(report);
    }

    // READ - get all reports
    public List<PollutionReport> getAllReports() {
        return reportRepository.findAll();
    }

    // DELETE - remove report (for Clean Up button)
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}