package com.studentintel.platform.semester;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SemesterRepository extends JpaRepository<Semester, Long> {

    Optional<Semester> findByAcademicYearAndTerm(String academicYear, String term);

    boolean existsByAcademicYearAndTerm(String academicYear, String term);
}