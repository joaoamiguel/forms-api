package com.example.iaassessment.repository;

import com.example.iaassessment.entity.RoleEntity;
import com.example.iaassessment.entity.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(RoleName name);
}
