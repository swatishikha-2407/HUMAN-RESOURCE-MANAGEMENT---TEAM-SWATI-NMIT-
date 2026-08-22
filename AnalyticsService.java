package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    private final UserRepository users; private final LeaveRequestRepository leaves; private final AttendanceRepository attendance; private final PayrollRepository payroll;
    public AnalyticsService(UserRepository users,LeaveRequestRepository leaves,AttendanceRepository attendance,PayrollRepository payroll){this.users=users;this.leaves=leaves;this.attendance=attendance;this.payroll=payroll;}
    public ApiDtos.AnalyticsResponse dashboard(LocalDate from,LocalDate to){
        Map<String,Long> leaveByStatus=new LinkedHashMap<>(); for(LeaveStatus s:LeaveStatus.values()) leaveByStatus.put(s.name(),leaves.countByStatus(s));
        Map<String,Long> leaveByType=leaves.countByType().stream().collect(Collectors.toMap(x->((LeaveType)x[0]).name(),x->((Number)x[1]).longValue(),(a,b)->a,LinkedHashMap::new));
        Map<AttendanceStatus,Long> totals=new EnumMap<>(AttendanceStatus.class); attendance.countByStatus(from,to).forEach(x->totals.put((AttendanceStatus)x[0],((Number)x[1]).longValue()));
        List<Map<String,Object>> daily=attendance.dailyStatus(from,to).stream().map(x->{Map<String,Object> m=new LinkedHashMap<>();m.put("date",x[0]);m.put("status",x[1]);m.put("count",x[2]);return m;}).toList();
        List<Map<String,Object>> pay=payroll.payrollByPeriod().stream().map(x->{Map<String,Object> m=new LinkedHashMap<>();m.put("payPeriod",x[0]);m.put("netSalary",x[1]);return m;}).toList();
        return new ApiDtos.AnalyticsResponse(users.countByActiveTrue(),totals.getOrDefault(AttendanceStatus.PRESENT,0L),totals.getOrDefault(AttendanceStatus.ABSENT,0L),leaveByStatus,leaveByType,daily,pay);
    }
}
