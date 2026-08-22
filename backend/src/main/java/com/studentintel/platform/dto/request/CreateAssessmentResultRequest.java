package com.studentintel.platform.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record CreateAssessmentResultRequest(

        @NotNull(message = "Assessment ID is required")
        Long assessmentId,

        @NotNull(message = "Student ID is required")
        Long studentId,

        @NotNull(message = "Marks obtained is required")
        @DecimalMin(value = "0.0", message = "Marks cannot be negative")
        BigDecimal marksObtained,

        String grade) {
}