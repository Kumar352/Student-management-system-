package com.studentintel.platform.student;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByStudentNumber(String studentNumber);

    boolean existsByStudentNumber(String studentNumber);

    Optional<Student> findByUserId(Long userId);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM Student s
        JOIN s.user u
        WHERE s.id = :studentId
        AND u.email = :email
    """)
    boolean existsByIdAndUserEmail(
            @Param("studentId") Long studentId,
            @Param("email") String email);
}
