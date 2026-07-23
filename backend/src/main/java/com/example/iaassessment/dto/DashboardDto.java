package com.example.iaassessment.dto;

import java.util.List;

public record DashboardDto(DashboardSummaryDto summary, List<DashboardSubmissionDto> submissions) {}
