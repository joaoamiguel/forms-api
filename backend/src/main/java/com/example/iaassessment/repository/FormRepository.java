package com.example.iaassessment.repository;

import com.example.iaassessment.entity.FormEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<FormEntity, Long> {
    Optional<FormEntity> findByCode(String code);
}
