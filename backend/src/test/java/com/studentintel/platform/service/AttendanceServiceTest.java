package com.studentintel.platform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.studentintel.platform.attendance.AttendanceRecord;
import com.studentintel.platform.attendance.AttendanceRecordRepository;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRecordRepository attendanceRecordRepository;

    private AttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        attendanceService =
                new AttendanceService(attendanceRecordRepository);
    }

    @Test
    void noAttendanceRecordsReturnsZero() {

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of());

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.ZERO,
                result);
    }

    @Test
    void allPresentReturnsOneHundredPercent() {

        AttendanceRecord first = mock(AttendanceRecord.class);
        AttendanceRecord second = mock(AttendanceRecord.class);
        AttendanceRecord third = mock(AttendanceRecord.class);

        when(first.getStatus()).thenReturn("PRESENT");
        when(second.getStatus()).thenReturn("PRESENT");
        when(third.getStatus()).thenReturn("PRESENT");

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(first, second, third));

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.valueOf(100).setScale(2),
                result);
    }

    @Test
    void allAbsentReturnsZeroPercent() {

        AttendanceRecord first = mock(AttendanceRecord.class);
        AttendanceRecord second = mock(AttendanceRecord.class);
        AttendanceRecord third = mock(AttendanceRecord.class);

        when(first.getStatus()).thenReturn("ABSENT");
        when(second.getStatus()).thenReturn("ABSENT");
        when(third.getStatus()).thenReturn("ABSENT");

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(first, second, third));

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.ZERO.setScale(2),
                result);
    }

    @Test
    void mixedAttendanceReturnsCorrectPercentage() {

        AttendanceRecord first = mock(AttendanceRecord.class);
        AttendanceRecord second = mock(AttendanceRecord.class);
        AttendanceRecord third = mock(AttendanceRecord.class);
        AttendanceRecord fourth = mock(AttendanceRecord.class);

        when(first.getStatus()).thenReturn("PRESENT");
        when(second.getStatus()).thenReturn("PRESENT");
        when(third.getStatus()).thenReturn("ABSENT");
        when(fourth.getStatus()).thenReturn("ABSENT");

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(
                        first,
                        second,
                        third,
                        fourth));

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.valueOf(50).setScale(2),
                result);
    }

    @Test
    void presentStatusIsCaseInsensitive() {

        AttendanceRecord first = mock(AttendanceRecord.class);
        AttendanceRecord second = mock(AttendanceRecord.class);

        when(first.getStatus()).thenReturn("present");
        when(second.getStatus()).thenReturn("PRESENT");

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(first, second));

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.valueOf(100).setScale(2),
                result);
    }

    @Test
    void attendancePercentageIsRoundedToTwoDecimalPlaces() {

        AttendanceRecord first = mock(AttendanceRecord.class);
        AttendanceRecord second = mock(AttendanceRecord.class);
        AttendanceRecord third = mock(AttendanceRecord.class);

        when(first.getStatus()).thenReturn("PRESENT");
        when(second.getStatus()).thenReturn("ABSENT");
        when(third.getStatus()).thenReturn("ABSENT");

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(
                        first,
                        second,
                        third));

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.valueOf(33.33).setScale(2),
                result);
    }

    @Test
    void onlyPresentRecordsAreCounted() {

        AttendanceRecord first = mock(AttendanceRecord.class);
        AttendanceRecord second = mock(AttendanceRecord.class);
        AttendanceRecord third = mock(AttendanceRecord.class);
        AttendanceRecord fourth = mock(AttendanceRecord.class);
        AttendanceRecord fifth = mock(AttendanceRecord.class);

        when(first.getStatus()).thenReturn("PRESENT");
        when(second.getStatus()).thenReturn("PRESENT");
        when(third.getStatus()).thenReturn("LATE");
        when(fourth.getStatus()).thenReturn("ABSENT");
        when(fifth.getStatus()).thenReturn("EXCUSED");

        when(attendanceRecordRepository.findByStudentId(1L))
                .thenReturn(List.of(
                        first,
                        second,
                        third,
                        fourth,
                        fifth));

        BigDecimal result =
                attendanceService.calculateAttendancePercentage(1L);

        assertEquals(
                BigDecimal.valueOf(40).setScale(2),
                result);
    }
}