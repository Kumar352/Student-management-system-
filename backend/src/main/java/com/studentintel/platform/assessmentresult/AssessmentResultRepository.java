package com.studentintel.platform.assessmentresult;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentResultRepository
        extends JpaRepository<AssessmentResult, Long> {

    List<AssessmentResult> findByStudentId(Long studentId);

    List<AssessmentResult> findByAssessmentId(Long assessmentId);

    boolean existsByAssessmentIdAndStudentId(
            Long assessmentId,
            Long studentId);
}