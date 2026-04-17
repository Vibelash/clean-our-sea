package com.example.seasweepers.Repos;

import com.example.seasweepers.Models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByStatusIgnoreCase(String status);

}
