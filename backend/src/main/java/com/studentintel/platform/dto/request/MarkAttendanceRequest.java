package com.studentintel.platform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MarkAttendanceRequest(

        @NotNull(message = "Session ID is required")
        Long sessionId,

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotBlank(message = "Attendance status is required")
        String status) {
}