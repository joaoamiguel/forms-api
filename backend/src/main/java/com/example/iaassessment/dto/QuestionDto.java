package com.example.iaassessment.dto;

import com.example.iaassessment.entity.QuestionType;
import java.util.List;

public record QuestionDto(
        Long id,
        String questionKey,
        String label,
        QuestionType type,
        boolean required,
        boolean reversed,
        Integer displayOrder,
        String sectionTitle,
        String helpText,
        List<String> options
) {}
