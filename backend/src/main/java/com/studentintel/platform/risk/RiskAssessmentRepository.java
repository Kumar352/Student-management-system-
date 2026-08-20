package com.studentintel.platform.risk;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAssessmentRepository
        extends JpaRepository<RiskAssessment, Long> {

    List<RiskAssessment> findByStudentId(Long studentId);

    List<RiskAssessment> findByRiskLevel(String riskLevel);

    Optional<RiskAssessment> findTopByStudentIdOrderByCalculatedAtDesc(
            Long studentId);
}