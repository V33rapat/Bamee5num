package com.restaurant.demo.service;

import com.restaurant.demo.dto.ReportSummary;

public interface ReportService {
    ReportSummary getMonthlyReport(Integer month, Integer year);
}
