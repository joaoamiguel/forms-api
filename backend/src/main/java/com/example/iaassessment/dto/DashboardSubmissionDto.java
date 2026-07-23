package com.example.iaassessment.dto;

import java.time.Instant;
import java.util.List;

public record DashboardSubmissionDto(
        Long submissionId,
        String userName,
        String userEmail,
        String formCode,
        Instant submittedAt,
        Integer readinessScore,
        Integer literacyScore,
        Integer opportunityScore,
        String quadrant,
        Integer technicalScore,
        String technicalBand,
        List<QuestionAnswerDto> answers
) {}
