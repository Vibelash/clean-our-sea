package com.example.backend.repositories;

import com.example.backend.models.PollutionReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollutionReportRepository extends JpaRepository<PollutionReport, Long> {
}