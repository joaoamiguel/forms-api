package com.example.iaassessment.service.impl;

import com.example.iaassessment.dto.*;
import com.example.iaassessment.entity.*;
import com.example.iaassessment.repository.*;
import com.example.iaassessment.service.DashboardService;
import java.util.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final UserRepository userRepository; private final SubmissionRepository submissionRepository; private final AnswerRepository answerRepository; private final ScoreRepository scoreRepository;
    public DashboardServiceImpl(UserRepository userRepository, SubmissionRepository submissionRepository, AnswerRepository answerRepository, ScoreRepository scoreRepository){ this.userRepository=userRepository; this.submissionRepository=submissionRepository; this.answerRepository=answerRepository; this.scoreRepository=scoreRepository; }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public DashboardDto dashboard() {
        List<SubmissionEntity> subs = submissionRepository.findAll();
        List<DashboardSubmissionDto> rows = new ArrayList<>();
        for (SubmissionEntity sub : subs) {
            ScoreEntity score = scoreRepository.findBySubmission(sub).orElse(null);
            List<QuestionAnswerDto> answers = answerRepository.findBySubmission(sub).stream().map(a -> new QuestionAnswerDto(a.getQuestion().getQuestionKey(), a.getAnswerJson())).toList();
            rows.add(new DashboardSubmissionDto(sub.getId(), sub.getUser().getName(), sub.getUser().getEmail(), sub.getForm().getCode(), sub.getSubmittedAt(), score==null?null:score.getReadinessScore(), score==null?null:score.getLiteracyScore(), score==null?null:score.getOpportunityScore(), score==null?null:score.getQuadrant(), score==null?null:score.getTechnicalScore(), score==null?null:score.getTechnicalBand(), answers));
        }
        Map<String, Long> byForm = new LinkedHashMap<>();
        for (SubmissionEntity sub : subs) byForm.merge(sub.getForm().getCode(), 1L, Long::sum);
        Map<String, Long> byRole = new LinkedHashMap<>();
        userRepository.findAll().forEach(u -> u.getRoles().forEach(r -> byRole.merge(r.getName().name(), 1L, Long::sum)));
        return new DashboardDto(new DashboardSummaryDto(userRepository.count(), subs.size(), byForm, byRole), rows);
    }
}
