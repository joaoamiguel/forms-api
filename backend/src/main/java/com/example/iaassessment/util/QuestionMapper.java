package com.example.iaassessment.util;

import com.example.iaassessment.dto.QuestionDto;
import com.example.iaassessment.entity.QuestionEntity;
import java.util.List;

public class QuestionMapper {
    public static QuestionDto toDto(QuestionEntity q) {
        return new QuestionDto(q.getId(), q.getQuestionKey(), q.getLabel(), q.getType(), q.isRequired(), q.isReversed(), q.getDisplayOrder(), q.getSectionTitle(), q.getHelpText(), q.getOptions());
    }
    public static List<QuestionDto> toDtos(List<QuestionEntity> questions) { return questions.stream().map(QuestionMapper::toDto).toList(); }
}
