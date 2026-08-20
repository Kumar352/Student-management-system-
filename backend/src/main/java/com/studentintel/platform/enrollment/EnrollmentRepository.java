package com.studentintel.platform.enrollment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findBySemesterId(Long semesterId);

    boolean existsByStudentIdAndCourseIdAndSemesterId(
            Long studentId,
            Long courseId,
            Long semesterId);
}