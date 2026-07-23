package com.example.iaassessment.service.impl;

import com.example.iaassessment.entity.*;
import com.example.iaassessment.service.ScoringService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class ScoringServiceImpl implements ScoringService {
    private final ObjectMapper objectMapper;
    public ScoringServiceImpl(ObjectMapper objectMapper) { this.objectMapper = objectMapper; }

    @Override
    public ScoreResult score(FormEntity form, Map<String, Object> answers, Map<String, QuestionEntity> questionMap) {
        if ("FORM1".equals(form.getCode())) return scoreForm1(answers, questionMap);
        if ("FORM2".equals(form.getCode())) return scoreForm2(answers, questionMap);
        throw new IllegalArgumentException("Formulário desconhecido: " + form.getCode());
    }

    private ScoreResult scoreForm1(Map<String, Object> answers, Map<String, QuestionEntity> questionMap) {
        int m1 = scoreModule(answers, questionMap, "m1q1", "m1q2", "m1q3", "m1q4", "m1q5");
        int m2 = scoreModule(answers, questionMap, "m2q1", "m2q2", "m2q3", "m2q4", "m2q5");
        int m3 = scoreModule(answers, questionMap, "m3q1", "m3q2", "m3q3", "m3q4", "m3q5");
        int readiness = normalize(m1 + m2 + m3, 15, 72);

        int self = num(answers, "a1") + num(answers, "a2") + num(answers, "a4") + num(answers, "a5") + num(answers, "a6") + num(answers, "a7") + num(answers, "a8") + (6 - num(answers, "a9"));
        int selfN = (int)Math.round(((double)(self - 8) / (39 - 8)) * 100);
        int breadth = ((list(answers, "a3").contains("__none__") ? 0 : list(answers, "a3").size()) * 100) / 8;
        int objCorrect = countCorrect(answers, questionMap, List.of("c1","c2","c3","c4","c5","c6"));
        int objN = (objCorrect * 100) / 6;
        int literacy = (int)Math.round(0.50 * selfN + 0.15 * breadth + 0.35 * objN);

        double oppMean = Arrays.stream(new String[]{"b1","b2","b3","b4","b5","b6","b7","b8"}).mapToInt(k -> num(answers, k)).average().orElse(1.0);
        int opportunity = (int)Math.round(((oppMean - 1.0) / 4.0) * 100.0);
        int autoV = num(answers, "b9b");
        String autoLbl = autoV <= 2 ? "baixo" : autoV == 3 ? "médio" : "alto";
        String quadrant = quadrant(literacy, opportunity);
        return new ScoreResult(readiness, literacy, opportunity, quadrant, null, autoLbl, resultJson(readiness, literacy, opportunity, quadrant, autoLbl));
    }

    private ScoreResult scoreForm2(Map<String, Object> answers, Map<String, QuestionEntity> questionMap) {
        int likert = Arrays.stream(new String[]{"q42","q43","q44","q45","q47"}).mapToInt(k -> num(answers, k)).sum();
        int rev = 6 - num(answers, "q48");
        int ladders = num(answers, "q41") + num(answers, "q46");
        int raw = likert + rev + ladders;
        int idx = (int)Math.round(((double)(raw - 8) / 30.0) * 100.0);
        String[] band = band(idx);
        return new ScoreResult(null, null, null, null, idx, band[0], resultJson(null, null, null, null, null, idx, band[0]));
    }

    private int scoreModule(Map<String, Object> answers, Map<String, QuestionEntity> questionMap, String q1, String q2, String q3, String q4, String q5) {
        return num(answers, q1) + num(answers, q2) + num(answers, q3) + (6 - num(answers, q4)) + num(answers, q5);
    }
    private int countCorrect(Map<String, Object> answers, Map<String, QuestionEntity> qmap, List<String> keys) {
        int c=0; for (String k: keys) { QuestionEntity q=qmap.get(k); if (q!=null && Objects.equals(toInt(answers.get(k)), q.getCorrectOptionIndex())) c++; } return c;
    }
    private int num(Map<String,Object> answers, String key){ return toInt(answers.get(key)); }
    private List<String> list(Map<String,Object> answers, String key){ Object v=answers.get(key); if (v==null) return List.of(); if (v instanceof List<?> l) return l.stream().map(String::valueOf).toList(); if (v instanceof String s && s.startsWith("[")) { try { return objectMapper.readValue(s, List.class); } catch(Exception e){ return List.of(s);} } return List.of(String.valueOf(v)); }
    private int toInt(Object v){ if (v==null) throw new IllegalArgumentException("Resposta ausente"); if (v instanceof Number n) return n.intValue(); return Integer.parseInt(String.valueOf(v)); }
    private int normalize(int raw, int mn, int mx){ return (int)Math.round(((double)(raw-mn)/(double)(mx-mn))*100.0); }
    private String quadrant(int lit, int opp){ String L = level(lit), O = level(opp); if (L.equals("cinzenta") || O.equals("cinzenta")) return "Indefinido — revisar"; if (L.equals("alta") && O.equals("alta")) return "Acelerar já"; if (L.equals("alta") && O.equals("baixa")) return "Multiplicador"; if (L.equals("baixa") && O.equals("alta")) return "Prioridade de treinamento"; return "Menor prioridade agora"; }
    private String level(int x){ return x >= 60 ? "alta" : x <= 40 ? "baixa" : "cinzenta"; }
    private String[] band(int x){ if (x <= 40) return new String[]{"Base técnica frágil"}; if (x <= 65) return new String[]{"Em construção"}; return new String[]{"Base técnica sólida"}; }
    private String resultJson(Integer readiness, Integer literacy, Integer opportunity, String quadrant, String autoLbl){ try { return objectMapper.writeValueAsString(Map.of("readiness", readiness, "literacy", literacy, "opportunity", opportunity, "quadrant", quadrant, "autoLabel", autoLbl)); } catch(Exception e){ return "{}"; } }
    private String resultJson(Integer readiness, Integer literacy, Integer opportunity, String quadrant, String autoLbl, Integer techScore, String techBand){ try { Map<String,Object> m = new LinkedHashMap<>(); m.put("readiness", readiness); m.put("literacy", literacy); m.put("opportunity", opportunity); m.put("quadrant", quadrant); m.put("autoLabel", autoLbl); m.put("technicalScore", techScore); m.put("technicalBand", techBand); return objectMapper.writeValueAsString(m); } catch(Exception e){ return "{}"; } }
}
