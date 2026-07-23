package com.example.iaassessment.dto;

import java.time.Instant;
import java.util.List;

public record SubmissionResponse(
        Long submissionId,
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
