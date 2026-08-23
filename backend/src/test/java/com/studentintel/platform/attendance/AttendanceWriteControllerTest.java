package com.studentintel.platform.attendance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.studentintel.platform.student.Student;
import com.studentintel.platform.student.StudentRepository;

@ExtendWith(MockitoExtension.class)
class AttendanceWriteControllerTest {

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    @Mock
    private AttendanceSessionRepository attendanceSessionRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AttendanceSession attendanceSession;

    @Mock
    private Student student;

    @Mock
    private AttendanceRecord attendanceRecord;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        AttendanceWriteController controller =
                new AttendanceWriteController(
                        attendanceRecordRepository,
                        attendanceSessionRepository,
                        studentRepository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingSessionIdIsRejected() throws Exception {

        String json = """
                {
                    "studentId": 1,
                    "status": "PRESENT"
                }
                """;

        mockMvc.perform(
                post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingStudentIdIsRejected() throws Exception {

        String json = """
                {
                    "sessionId": 1,
                    "status": "PRESENT"
                }
                """;

        mockMvc.perform(
                post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void blankStatusIsRejected() throws Exception {

        String json = """
                {
                    "sessionId": 1,
                    "studentId": 1,
                    "status": ""
                }
                """;

        mockMvc.perform(
                post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sessionNotFoundReturnsBadRequest() throws Exception {

        when(attendanceSessionRepository.findById(999L))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "sessionId": 999,
                    "studentId": 1,
                    "status": "PRESENT"
                }
                """;

        mockMvc.perform(
                post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void studentNotFoundReturnsBadRequest() throws Exception {

        when(attendanceSessionRepository.findById(1L))
                .thenReturn(Optional.of(attendanceSession));

        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        String json = """
                {
                    "sessionId": 1,
                    "studentId": 999,
                    "status": "PRESENT"
                }
                """;

        mockMvc.perform(
                post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validAttendanceIsSavedSuccessfully() throws Exception {

        when(attendanceSessionRepository.findById(1L))
                .thenReturn(Optional.of(attendanceSession));

        when(studentRepository.findById(1L))
                .thenReturn(Optional.of(student));

        when(attendanceRecordRepository.save(any(AttendanceRecord.class)))
                .thenReturn(attendanceRecord);

        String json = """
                {
                    "sessionId": 1,
                    "studentId": 1,
                    "status": "PRESENT"
                }
                """;

        mockMvc.perform(
                post("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(attendanceRecordRepository)
                .save(any(AttendanceRecord.class));
    }
}