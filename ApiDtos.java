package com.example.leavemanagement.dto;

import com.example.leavemanagement.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public final class ApiDtos {
    private ApiDtos() {}
    public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {}
    public record LoginResponse(String token, Long userId, String email, Role role) {}
    public record LeaveApplicationRequest(@NotNull LeaveType leaveType, @NotNull LocalDate startDate, @NotNull LocalDate endDate, @Size(max=1000) String remarks) {}
    public record LeaveReviewRequest(@NotNull LeaveStatus status, @Size(max=1000) String adminComment) {}
    public record PayrollRequest(@NotBlank @Pattern(regexp="\\d{4}-(0[1-9]|1[0-2])") String payPeriod, @NotNull @PositiveOrZero BigDecimal basicSalary, @NotNull @PositiveOrZero BigDecimal allowances, @NotNull @PositiveOrZero BigDecimal deductions) {}
    public record AttendanceRequest(@NotNull LocalDate attendanceDate, @NotNull AttendanceStatus status, LocalTime checkIn, LocalTime checkOut, @Size(max=500) String notes) {}
    public record LeaveResponse(Long id, String employeeCode, LeaveType leaveType, LocalDate startDate, LocalDate endDate, int days, String remarks, LeaveStatus status, String adminComment, LocalDateTime createdAt) {}
    public record PayrollResponse(Long id, String employeeCode, String payPeriod, BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions, BigDecimal netSalary) {}
    public record AttendanceResponse(Long id, String employeeCode, LocalDate attendanceDate, AttendanceStatus status, LocalTime checkIn, LocalTime checkOut) {}
    public record AnalyticsResponse(long employeeCount, long presentCount, long absentCount, Map<String,Long> leaveByStatus, Map<String,Long> leaveByType, List<Map<String,Object>> attendanceDaily, List<Map<String,Object>> payrollOverview) {}
}
