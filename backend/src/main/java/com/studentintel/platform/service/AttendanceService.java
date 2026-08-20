package com.studentintel.platform.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;

import com.studentintel.platform.attendance.AttendanceRecord;
import com.studentintel.platform.attendance.AttendanceRecordRepository;

@Service
public class AttendanceService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceService(
            AttendanceRecordRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public BigDecimal calculateAttendancePercentage(Long studentId) {

        List<AttendanceRecord> records =
                attendanceRecordRepository.findByStudentId(studentId);

        if (records.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long present = records.stream()
                .filter(record -> "PRESENT".equalsIgnoreCase(record.getStatus()))
                .count();

        return BigDecimal.valueOf(present)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        BigDecimal.valueOf(records.size()),
                        2,
                        RoundingMode.HALF_UP);
    }
}