package com.example.iaassessment.service.impl;

import com.example.iaassessment.dto.*;
import com.example.iaassessment.entity.*;
import com.example.iaassessment.repository.*;
import com.example.iaassessment.security.CustomUserDetails;
import com.example.iaassessment.service.ScoringService;
import com.example.iaassessment.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final FormRepository formRepository; private final QuestionRepository questionRepository; private final SubmissionRepository submissionRepository; private final AnswerRepository answerRepository; private final ScoreRepository scoreRepository; private final ScoringService scoringService; private final ObjectMapper objectMapper;
    public SubmissionServiceImpl(FormRepository formRepository, QuestionRepository questionRepository, SubmissionRepository submissionRepository, AnswerRepository answerRepository, ScoreRepository scoreRepository, ScoringService scoringService, ObjectMapper objectMapper) { this.formRepository=formRepository; this.questionRepository=questionRepository; this.submissionRepository=submissionRepository; this.answerRepository=answerRepository; this.scoreRepository=scoreRepository; this.scoringService=scoringService; this.objectMapper=objectMapper; }

    @Override
    @Transactional
    public SubmissionResponse submit(String formCode, SubmissionRequest request) {
        UserEntity user = currentUser(); FormEntity form = formRepository.findByCode(formCode).orElseThrow(() -> new IllegalArgumentException("Formulário não encontrado")); enforceAccess(user, form);
        submissionRepository.findByUserAndForm(user, form).ifPresent(existing -> { throw new IllegalStateException("Usuário já respondeu este formulário"); });
        Map<String, Object> answers = request.answers();
        Map<String, QuestionEntity> qmap = questionRepository.findByFormOrderByDisplayOrderAscIdAsc(form).stream().collect(java.util.stream.Collectors.toMap(QuestionEntity::getQuestionKey, q -> q));
        validateRequired(qmap, answers);
        ScoringService.ScoreResult result = scoringService.score(form, answers, qmap);
        SubmissionEntity sub = new SubmissionEntity(); sub.setUser(user); sub.setForm(form); sub.setSubmittedAt(Instant.now()); sub = submissionRepository.save(sub);
        List<AnswerEntity> savedAnswers = new ArrayList<>();
        for (QuestionEntity q : qmap.values()) {
            AnswerEntity a = new AnswerEntity(); a.setSubmission(sub); a.setQuestion(q); a.setAnswerJson(toJson(answers.get(q.getQuestionKey()))); savedAnswers.add(a);
        }
        answerRepository.saveAll(savedAnswers);
        ScoreEntity score = new ScoreEntity(); score.setSubmission(sub); score.setReadinessScore(result.readinessScore()); score.setLiteracyScore(result.literacyScore()); score.setOpportunityScore(result.opportunityScore()); score.setQuadrant(result.quadrant()); score.setTechnicalScore(result.technicalScore()); score.setTechnicalBand(result.technicalBand()); score.setResultJson(result.resultJson()); scoreRepository.save(score);
        return toResponse(sub, score, savedAnswers);
    }

    @Override
    @Transactional(readOnly = true)
    public SubmissionResponse latestMine(String formCode) {
        UserEntity user = currentUser(); FormEntity form = formRepository.findByCode(formCode).orElseThrow(); enforceAccess(user, form); SubmissionEntity sub = submissionRepository.findByUserAndForm(user, form).orElseThrow(() -> new IllegalStateException("Sem resposta ainda")); ScoreEntity score = scoreRepository.findBySubmission(sub).orElseThrow(); List<AnswerEntity> answers = answerRepository.findBySubmission(sub); return toResponse(sub, score, answers);
    }

    private void validateRequired(Map<String, QuestionEntity> qmap, Map<String, Object> answers) {
        for (QuestionEntity q : qmap.values()) {
            if (!q.isRequired()) continue;
            if (!answers.containsKey(q.getQuestionKey()) || answers.get(q.getQuestionKey()) == null || String.valueOf(answers.get(q.getQuestionKey())).isBlank()) throw new IllegalArgumentException("Resposta obrigatória ausente: " + q.getQuestionKey());
        }
    }

    private void enforceAccess(UserEntity user, FormEntity form) {
        Set<RoleName> userRoles = user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet());
        boolean allowed = form.getAllowedRoles().isEmpty() || form.getAllowedRoles().stream().anyMatch(userRoles::contains);
        if (!allowed) throw new AccessDeniedException("Você não possui acesso a este formulário");
    }

    private UserEntity currentUser(){ return ((CustomUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser(); }
    private String toJson(Object value){ try { return objectMapper.writeValueAsString(value); } catch(Exception e){ return String.valueOf(value); } }
    private SubmissionResponse toResponse(SubmissionEntity sub, ScoreEntity score, List<AnswerEntity> answers){
        List<QuestionAnswerDto> ans = answers.stream().map(a -> new QuestionAnswerDto(a.getQuestion().getQuestionKey(), a.getAnswerJson())).toList();
        return new SubmissionResponse(sub.getId(), sub.getForm().getCode(), sub.getSubmittedAt(), score.getReadinessScore(), score.getLiteracyScore(), score.getOpportunityScore(), score.getQuadrant(), score.getTechnicalScore(), score.getTechnicalBand(), ans);
    }
}
