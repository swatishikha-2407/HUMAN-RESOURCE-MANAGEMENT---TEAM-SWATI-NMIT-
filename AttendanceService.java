package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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
}
