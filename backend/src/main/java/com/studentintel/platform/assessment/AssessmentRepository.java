package com.studentintel.platform.assessment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByCourseId(Long courseId);

    List<Assessment> findBySemesterId(Long semesterId);
}