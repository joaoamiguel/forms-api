package com.example.iaassessment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record SubmissionRequest(@NotNull Map<String, Object> answers) {}
