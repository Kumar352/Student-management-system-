package com.studentintel.platform.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.studentintel.platform.exception.GlobalExceptionHandler;
import com.studentintel.platform.program.Program;
import com.studentintel.platform.program.ProgramRepository;
import com.studentintel.platform.user.User;
import com.studentintel.platform.user.UserRepository;

@ExtendWith(MockitoExtension.class)
class StudentWriteControllerTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private User user;

    @Mock
    private Program program;

    @Mock
    private Student student;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        StudentWriteController controller =
                new StudentWriteController(
                        studentRepository,
                        userRepository,
                        programRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingUserIdIsRejected() throws Exception {

        String json = """
                {
                    "studentNumber": "STU001",
                    "programId": 1,
                    "admissionYear": 2025,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankStudentNumberIsRejected() throws Exception {

        String json = """
                {
                    "userId": 1,
                    "studentNumber": "",
                    "programId": 1,
                    "admissionYear": 2025,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingProgramIdIsRejected() throws Exception {

        String json = """
                {
                    "userId": 1,
                    "studentNumber": "STU001",
                    "admissionYear": 2025,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidAdmissionYearIsRejected() throws Exception {

        String json = """
                {
                    "userId": 1,
                    "studentNumber": "STU001",
                    "programId": 1,
                    "admissionYear": 1999,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidCurrentSemesterIsRejected() throws Exception {

        String json = """
                {
                    "userId": 1,
                    "studentNumber": "STU001",
                    "programId": 1,
                    "admissionYear": 2025,
                    "currentSemester": 0
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userNotFoundReturnsBadRequest() throws Exception {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "userId": 999,
                    "studentNumber": "STU001",
                    "programId": 1,
                    "admissionYear": 2025,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void programNotFoundReturnsBadRequest() throws Exception {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(programRepository.findById(999L))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "userId": 1,
                    "studentNumber": "STU001",
                    "programId": 999,
                    "admissionYear": 2025,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validStudentCreationReturnsOk() throws Exception {

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(programRepository.findById(1L))
                .thenReturn(Optional.of(program));

        when(program.getId())
                .thenReturn(1L);

        when(studentRepository.save(any(Student.class)))
                .thenReturn(student);

        when(student.getId())
                .thenReturn(1L);

        when(student.getStudentNumber())
                .thenReturn("STU001");

        when(student.getProgram())
                .thenReturn(program);

        when(student.getCurrentSemester())
                .thenReturn(1);

        when(student.getStatus())
                .thenReturn("ACTIVE");

        String json = """
                {
                    "userId": 1,
                    "studentNumber": "STU001",
                    "programId": 1,
                    "admissionYear": 2025,
                    "currentSemester": 1
                }
                """;

        mockMvc.perform(
                post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(studentRepository)
                .save(any(Student.class));
    }

    @Test
    void updateSemesterForExistingStudentReturnsOk() throws Exception {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(studentRepository.save(student))
                .thenReturn(student);

        when(student.getId())
                .thenReturn(1L);

        when(student.getStudentNumber())
                .thenReturn("STU001");

        when(student.getProgram())
                .thenReturn(program);

        when(program.getId())
                .thenReturn(1L);

        when(student.getCurrentSemester())
                .thenReturn(3);

        when(student.getStatus())
                .thenReturn("ACTIVE");

        mockMvc.perform(
                put("/api/students/1/semester")
                        .param("semester", "3"))
                .andExpect(status().isOk());

        verify(studentRepository)
                .save(student);
    }

    @Test
    void updateSemesterForMissingStudentReturnsNotFound()
            throws Exception {

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                put("/api/students/999/semester")
                        .param("semester", "3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatusForExistingStudentReturnsOk() throws Exception {

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(studentRepository.save(student))
                .thenReturn(student);

        when(student.getId())
                .thenReturn(1L);

        when(student.getStudentNumber())
                .thenReturn("STU001");

        when(student.getProgram())
                .thenReturn(program);

        when(program.getId())
                .thenReturn(1L);

        when(student.getCurrentSemester())
                .thenReturn(3);

        when(student.getStatus())
                .thenReturn("INACTIVE");

        mockMvc.perform(
                put("/api/students/1/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isOk());

        verify(studentRepository)
                .save(student);
    }

    @Test
    void updateStatusForMissingStudentReturnsNotFound()
            throws Exception {

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                put("/api/students/999/status")
                        .param("status", "INACTIVE"))
                .andExpect(status().isNotFound());
    }
}