package com.example.iaassessment.dto;

import java.util.Map;

public record DashboardSummaryDto(long totalUsers, long totalSubmissions, Map<String, Long> submissionsByForm, Map<String, Long> usersByRole) {}
