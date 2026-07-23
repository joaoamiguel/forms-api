package com.example.iaassessment.repository;

import com.example.iaassessment.entity.AnswerEntity;
import com.example.iaassessment.entity.SubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
    List<AnswerEntity> findBySubmission(SubmissionEntity submission);
}
