package com.example.iaassessment.repository;

import com.example.iaassessment.entity.FormEntity;
import com.example.iaassessment.entity.SubmissionEntity;
import com.example.iaassessment.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<SubmissionEntity, Long> {
    Optional<SubmissionEntity> findByUserAndForm(UserEntity user, FormEntity form);
    List<SubmissionEntity> findByUser(UserEntity user);
    List<SubmissionEntity> findByForm(FormEntity form);
}
