package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.service.*;
import jakarta.validation.Valid;
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
}
