package com.studentintel.platform.dto;

import com.studentintel.platform.student.Student;

public record StudentResponse(
        Long id,
        String studentNumber,
        Long programId,
        Integer currentSemester,
        String status) {

    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getStudentNumber(),
                student.getProgram().getId(),
                student.getCurrentSemester(),
                student.getStatus());
    }
}
