package com.example.iaassessment.service;

import com.example.iaassessment.entity.FormEntity;
import com.example.iaassessment.entity.QuestionEntity;
import java.util.Map;

public interface ScoringService {
    ScoreResult score(FormEntity form, Map<String, Object> answers, Map<String, QuestionEntity> questionMap);

    record ScoreResult(Integer readinessScore, Integer literacyScore, Integer opportunityScore, String quadrant, Integer technicalScore, String technicalBand, String resultJson) {}
}
