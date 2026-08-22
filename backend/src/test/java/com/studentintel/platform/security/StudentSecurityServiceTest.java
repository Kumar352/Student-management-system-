package com.studentintel.platform.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.studentintel.platform.student.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentSecurityServiceTest {

    @Mock
    private StudentRepository studentRepository;

    private StudentSecurityService studentSecurityService;

    @BeforeEach
    void setUp() {
        studentSecurityService =
                new StudentSecurityService(studentRepository);
    }

    @Test
    void studentCanAccessOwnStudentRecord() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "student@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("STUDENT")));

        when(studentRepository.existsByIdAndUserEmail(
                1L,
                "student@test.com"))
                .thenReturn(true);

        boolean result =
                studentSecurityService.canAccessStudent(
                        1L,
                        authentication);

        assertTrue(result);
    }

    @Test
    void studentCannotAccessAnotherStudentRecord() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "student@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("STUDENT")));

        when(studentRepository.existsByIdAndUserEmail(
                2L,
                "student@test.com"))
                .thenReturn(false);

        boolean result =
                studentSecurityService.canAccessStudent(
                        2L,
                        authentication);

        assertFalse(result);
    }

    @Test
    void facultyCanAccessAnyStudent() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "faculty@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("FACULTY")));

        boolean result =
                studentSecurityService.canAccessStudent(
                        999L,
                        authentication);

        assertTrue(result);
    }

    @Test
    void adminCanAccessAnyStudent() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ADMIN")));

        boolean result =
                studentSecurityService.canAccessStudent(
                        999L,
                        authentication);

        assertTrue(result);
    }

    @Test
    void unauthenticatedUserCannotAccessStudent() {

        boolean result =
                studentSecurityService.canAccessStudent(
                        1L,
                        null);

        assertFalse(result);
    }

    @Test
    void studentCannotAccessNonexistentStudent() {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "student@test.com",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("STUDENT")));

        when(studentRepository.existsByIdAndUserEmail(
                999L,
                "student@test.com"))
                .thenReturn(false);

        boolean result =
                studentSecurityService.canAccessStudent(
                        999L,
                        authentication);

        assertFalse(result);
    }
}