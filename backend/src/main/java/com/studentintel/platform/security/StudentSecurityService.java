package com.studentintel.platform.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.studentintel.platform.student.StudentRepository;

@Service("studentSecurityService")
public class StudentSecurityService {

    private final StudentRepository studentRepository;

    public StudentSecurityService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public boolean canAccessStudent(
            Long studentId,
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        String role = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .findFirst()
                .orElse("");

        // Faculty and Admin can access any student.
        if ("FACULTY".equals(role) ||
                "ADMIN".equals(role)) {
            return true;
        }

        // Students can access only their own record.
        return studentRepository.existsByIdAndUserEmail(
                studentId,
                authentication.getName());
    }
}