package com.example.iaassessment.repository;

import com.example.iaassessment.entity.FormEntity;
import com.example.iaassessment.entity.QuestionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findByFormOrderByDisplayOrderAscIdAsc(FormEntity form);
}
