package com.example.iaassessment.repository;

import com.example.iaassessment.entity.ScoreEntity;
import com.example.iaassessment.entity.SubmissionEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreRepository extends JpaRepository<ScoreEntity, Long> {
    Optional<ScoreEntity> findBySubmission(SubmissionEntity submission);
}
