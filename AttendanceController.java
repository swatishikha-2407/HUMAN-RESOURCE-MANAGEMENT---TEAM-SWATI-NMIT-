package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
package com.example.employeeattendance.controller;

import com.example.employeeattendance.dto.AttendanceDtos;
import com.example.employeeattendance.entity.Enums;
import com.example.employeeattendance.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendance; private final AuthService auth;
    public AttendanceController(AttendanceService attendance,AuthService auth){this.attendance=attendance;this.auth=auth;}
    @GetMapping("/mine") @PreAuthorize("hasRole('EMPLOYEE')") public List<ApiDtos.AttendanceResponse> mine(@RequestParam(required=false) LocalDate from,@RequestParam(required=false) LocalDate to,Authentication a){LocalDate end=to==null?LocalDate.now():to; LocalDate start=from==null?end.withDayOfMonth(1):from; return attendance.employeeHistory(auth.current(a.getName()),start,end);}
    @PutMapping("/employees/{employeeId}") @PreAuthorize("hasRole('ADMIN')") public ApiDtos.AttendanceResponse upsert(@PathVariable Long employeeId,@Valid @RequestBody ApiDtos.AttendanceRequest r){return attendance.upsert(employeeId,r);}
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/check-in")
    public AttendanceDtos.AttendanceResponse checkIn(Authentication authentication) {
        return attendanceService.checkIn(authentication.getName());
    }

    @PostMapping("/check-out")
    public AttendanceDtos.AttendanceResponse checkOut(Authentication authentication) {
        return attendanceService.checkOut(authentication.getName());
    }

    @GetMapping("/me/today")
    public AttendanceDtos.AttendanceResponse today(Authentication authentication) {
        return attendanceService.today(authentication.getName());
    }

    @GetMapping("/me/history")
    public List<AttendanceDtos.AttendanceResponse> myHistory(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate end = to == null ? LocalDate.now() : to;
        LocalDate start = from == null ? end.minusDays(30) : from;
        validateRange(start, end);
        return attendanceService.ownHistory(authentication.getName(), start, end);
    }

    @GetMapping("/me/weekly")
    public List<AttendanceDtos.AttendanceResponse> weekly(
            Authentication authentication,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        LocalDate start = AttendanceService.startOfWeek(weekStart == null ? LocalDate.now() : weekStart);
        return attendanceService.ownHistory(authentication.getName(), start, start.plusDays(6));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping("/all")
    public List<AttendanceDtos.AttendanceResponse> all(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        LocalDate end = to == null ? LocalDate.now() : to;
        LocalDate start = from == null ? end : from;
        validateRange(start, end);
        return attendanceService.allHistory(start, end);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PatchMapping("/{attendanceId}/status")
    public AttendanceDtos.AttendanceResponse updateStatus(
            @PathVariable Long attendanceId,
            @Valid @RequestBody AttendanceDtos.UpdateAttendanceRequest request) {
        return attendanceService.updateStatus(attendanceId, request.status());
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new com.example.employeeattendance.exception.ApiException(
                    HttpStatus.BAD_REQUEST, "from must be on or before to");
        }
        if (from.plusDays(366).isBefore(to)) {
            throw new com.example.employeeattendance.exception.ApiException(
                    HttpStatus.BAD_REQUEST, "Date range cannot exceed one year");
        }
    }
}
