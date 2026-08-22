package com.studentintel.platform.student;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class StudentOwnershipApiTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(
                        org.springframework.security.test.web.servlet.setup
                                .SecurityMockMvcConfigurers
                                .springSecurity()
                )
                .build();
    }

    @Test
    void unauthenticatedStudentDashboardRequestIsRejected()
            throws Exception {

        mockMvc.perform(
                get("/api/students/1/dashboard/performance"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unauthenticatedIntelligenceRequestIsRejected()
            throws Exception {

        mockMvc.perform(
                get("/api/students/1/intelligence"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void studentCannotAccessProtectedDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/students/1/dashboard/performance")
                        .with(user("student@test.com")
                                .authorities(
                                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                "STUDENT"))))
                .andExpect(result ->
                        org.junit.jupiter.api.Assertions.assertTrue(
                                result.getResolvedException() == null
                                        || result.getResolvedException()
                                                instanceof org.springframework.security.authorization.AuthorizationDeniedException
                        ));
    }

    @Test
    void authenticatedFacultyCanReachProtectedDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/students/1/dashboard/performance")
                        .with(user("faculty@test.com")
                                .authorities(
                                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                "FACULTY"))))
                .andExpect(status().isOk());
    }

    @Test
    void authenticatedAdminCanReachProtectedDashboard()
            throws Exception {

        mockMvc.perform(
                get("/api/students/1/dashboard/performance")
                        .with(user("admin@test.com")
                                .authorities(
                                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                "ADMIN"))))
                .andExpect(status().isOk());
    }
}