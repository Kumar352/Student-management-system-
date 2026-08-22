package com.studentintel.platform.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStudentRequest(

        @NotNull(message = "User ID is required")
        Long userId,

        @NotBlank(message = "Student number is required")
        String studentNumber,

        @NotNull(message = "Program ID is required")
        Long programId,

        LocalDate dateOfBirth,

        @NotNull(message = "Admission year is required")
        @Min(value = 2000, message = "Admission year must be valid")
        Integer admissionYear,

        @NotNull(message = "Current semester is required")
        @Min(value = 1, message = "Current semester must be at least 1")
        Integer currentSemester) {
}