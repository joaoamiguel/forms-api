package com.example.iaassessment.service.impl;

import com.example.iaassessment.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScoringServiceTest {

    private final ScoringServiceImpl service = new ScoringServiceImpl(new ObjectMapper());

    @Test
    void calculatesForm1Scores() {
        FormEntity form = new FormEntity();
        form.setCode("FORM1");
        Map<String, Object> answers = new HashMap<>();
        answers.put("m1q1", 5); answers.put("m1q2", 5); answers.put("m1q3", 5); answers.put("m1q4", 5); answers.put("m1q5", 4);
        answers.put("m2q1", 5); answers.put("m2q2", 5); answers.put("m2q3", 5); answers.put("m2q4", 5); answers.put("m2q5", 4);
        answers.put("m3q1", 5); answers.put("m3q2", 5); answers.put("m3q3", 5); answers.put("m3q4", 5); answers.put("m3q5", 4);
        answers.put("a1", 5); answers.put("a2", 4); answers.put("a3", List.of("Escrever/revisar textos","Resumir documentos","Gerar ideias","Planilhas/dados","Traduzir","Apresentações/materiais","Buscar informação","Programar"));
        answers.put("a4", 5); answers.put("a5", 5); answers.put("a6", 5); answers.put("a7", 5); answers.put("a8", 5); answers.put("a9", 1);
        answers.put("b1", 5); answers.put("b2", 5); answers.put("b3", 5); answers.put("b4", 5); answers.put("b5", 5); answers.put("b6", 5); answers.put("b7", 5); answers.put("b8", 5); answers.put("b9b", 4);
        answers.put("c1", 1); answers.put("c2", 2); answers.put("c3", 1); answers.put("c4", 0); answers.put("c5", 1); answers.put("c6", 1);

        Map<String, QuestionEntity> qmap = new HashMap<>();
        int[] correct = {1,2,1,0,1,1};
        for (int i = 0; i < 6; i++) {
            String k = List.of("c1","c2","c3","c4","c5","c6").get(i);
            QuestionEntity q = new QuestionEntity();
            q.setQuestionKey(k);
            q.setCorrectOptionIndex(correct[i]);
            qmap.put(k, q);
        }
        var result = service.score(form, answers, qmap);
        assertEquals(79, result.readinessScore());
        assertEquals(100, result.literacyScore());
        assertEquals(100, result.opportunityScore());
        assertEquals("Acelerar já", result.quadrant());
    }

    @Test
    void calculatesForm2Scores() {
        FormEntity form = new FormEntity();
        form.setCode("FORM2");
        Map<String, Object> answers = new HashMap<>();
        answers.put("q41", 4); answers.put("q42", 5); answers.put("q43", 5); answers.put("q44", 5); answers.put("q45", 5); answers.put("q46", 4); answers.put("q47", 5); answers.put("q48", 1);
        var result = service.score(form, answers, Map.of());
        assertEquals(100, result.technicalScore());
        assertEquals("Base técnica sólida", result.technicalBand());
    }
}
