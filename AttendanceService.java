package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
package com.example.employeeattendance.service;

import com.example.employeeattendance.dto.AttendanceDtos;
import com.example.employeeattendance.entity.Attendance;
import com.example.employeeattendance.entity.Enums;
import com.example.employeeattendance.entity.User;
import com.example.employeeattendance.exception.ApiException;
import com.example.employeeattendance.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository attendance; private final UserRepository users;
    public AttendanceService(AttendanceRepository attendance,UserRepository users){this.attendance=attendance;this.users=users;}
    public List<ApiDtos.AttendanceResponse> employeeHistory(User u,LocalDate from,LocalDate to){return attendance.findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(u.getId(),from,to).stream().map(this::toResponse).toList();}
    @Transactional public ApiDtos.AttendanceResponse upsert(Long employeeId,ApiDtos.AttendanceRequest r){
        User employee=users.findById(employeeId).orElseThrow(()->new IllegalArgumentException("Employee not found"));
        AttendanceRecord a=attendance.findByEmployeeIdAndAttendanceDate(employeeId,r.attendanceDate()).orElseGet(AttendanceRecord::new);
        a.setEmployee(employee); a.setAttendanceDate(r.attendanceDate()); a.setStatus(r.status()); a.setCheckIn(r.checkIn()); a.setCheckOut(r.checkOut()); a.setNotes(r.notes()); return toResponse(attendance.save(a));
    }
    private ApiDtos.AttendanceResponse toResponse(AttendanceRecord a){return new ApiDtos.AttendanceResponse(a.getId(),a.getEmployee().getEmployeeCode(),a.getAttendanceDate(),a.getStatus(),a.getCheckIn(),a.getCheckOut());}
    private final AttendanceRepository attendanceRepository;
    private final EmployeeService employeeService;

    public AttendanceService(AttendanceRepository attendanceRepository, EmployeeService employeeService) {
        this.attendanceRepository = attendanceRepository;
        this.employeeService = employeeService;
    }

    @Transactional
    public AttendanceDtos.AttendanceResponse checkIn(String email) {
        User employee = employeeService.getUserByEmail(email);
        LocalDate today = LocalDate.now();
        if (attendanceRepository.findByEmployeeEmployeeIdAndDate(employee.getEmployeeId(), today).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already checked in today");
        }
        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setDate(today);
        attendance.setCheckIn(LocalDateTime.now());
        attendance.setStatus(Enums.AttendanceStatus.PRESENT);
        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional
    public AttendanceDtos.AttendanceResponse checkOut(String email) {
        User employee = employeeService.getUserByEmail(email);
        Attendance attendance = attendanceRepository
                .findByEmployeeEmployeeIdAndDate(employee.getEmployeeId(), LocalDate.now())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "Check in before checking out"));
        if (attendance.getCheckOut() != null) {
            throw new ApiException(HttpStatus.CONFLICT, "You have already checked out today");
        }
        attendance.setCheckOut(LocalDateTime.now());
        return toResponse(attendanceRepository.save(attendance));
    }

    @Transactional(readOnly = true)
    public AttendanceDtos.AttendanceResponse today(String email) {
        User employee = employeeService.getUserByEmail(email);
        return attendanceRepository.findByEmployeeEmployeeIdAndDate(employee.getEmployeeId(), LocalDate.now())
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<AttendanceDtos.AttendanceResponse> ownHistory(String email, LocalDate from, LocalDate to) {
        User employee = employeeService.getUserByEmail(email);
        return attendanceRepository.findByEmployeeEmployeeIdAndDateBetweenOrderByDateDesc(
                        employee.getEmployeeId(), from, to).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceDtos.AttendanceResponse> allHistory(LocalDate from, LocalDate to) {
        return attendanceRepository.findByDateBetweenOrderByDateDesc(from, to)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public AttendanceDtos.AttendanceResponse updateStatus(Long attendanceId, Enums.AttendanceStatus status) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Attendance record not found"));
        attendance.setStatus(status);
        return toResponse(attendanceRepository.save(attendance));
    }

    public static LocalDate startOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private AttendanceDtos.AttendanceResponse toResponse(Attendance attendance) {
        String employeeName = attendance.getEmployee().getProfile() == null
                ? null : attendance.getEmployee().getProfile().getName();
        return new AttendanceDtos.AttendanceResponse(
                attendance.getId(), attendance.getEmployee().getEmployeeId(), employeeName,
                attendance.getDate().toString(),
                attendance.getCheckIn() == null ? null : attendance.getCheckIn().toString(),
                attendance.getCheckOut() == null ? null : attendance.getCheckOut().toString(),
                attendance.getStatus());
    }
}
