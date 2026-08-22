package com.example.employeeattendance.dto;

import com.example.employeeattendance.entity.Enums;
import jakarta.validation.constraints.NotNull;

public final class AttendanceDtos {
    private AttendanceDtos() {}

    public record AttendanceResponse(
            Long id,
            String employeeId,
            String employeeName,
            String date,
            String checkIn,
            String checkOut,
            Enums.AttendanceStatus status
    ) {}

    public record UpdateAttendanceRequest(
            @NotNull Enums.AttendanceStatus status
    ) {}
}
