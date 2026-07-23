package com.example.iaassessment.dto;

import java.util.List;

public record FormDto(Long id, String code, String title, String description, List<String> allowedRoles, List<QuestionDto> questions) {}
