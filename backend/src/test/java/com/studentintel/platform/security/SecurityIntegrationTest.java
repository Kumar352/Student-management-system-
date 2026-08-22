package com.studentintel.platform.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class SecurityIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(
                        org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
                                .springSecurity()
                )
                .build();
    }

    @Test
    void unauthenticatedRequestShouldBeRejected() throws Exception {

        mockMvc.perform(
                get("/api/students"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            username = "student@test.com",
            authorities = {"STUDENT"})
    void authenticatedStudentCanAccessStudentApi() throws Exception {

        mockMvc.perform(
                get("/api/students"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "faculty@test.com",
            authorities = {"FACULTY"})
    void facultyCanAccessStudentApi() throws Exception {

        mockMvc.perform(
                get("/api/students"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "admin@test.com",
            authorities = {"ADMIN"})
    void adminCanAccessUserApi() throws Exception {

        mockMvc.perform(
                get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(
            username = "student@test.com",
            authorities = {"STUDENT"})
    void studentCannotAccessUserApi() throws Exception {

        mockMvc.perform(
                get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(
            username = "student@test.com",
            authorities = {"STUDENT"})
    void studentCannotAccessFacultyApi() throws Exception {

        mockMvc.perform(
                get("/api/faculty"))
                .andExpect(status().isForbidden());
    }
}