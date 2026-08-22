package com.example.leavemanagement.config;

import com.example.leavemanagement.model.AttendanceRecord;
import com.example.leavemanagement.model.AttendanceStatus;
import com.example.leavemanagement.model.LeaveRequest;
import com.example.leavemanagement.model.LeaveType;
import com.example.leavemanagement.model.PayrollRecord;
import com.example.leavemanagement.model.Role;
import com.example.leavemanagement.model.User;
import com.example.leavemanagement.repository.AttendanceRepository;
import com.example.leavemanagement.repository.LeaveRequestRepository;
import com.example.leavemanagement.repository.PayrollRepository;
import com.example.leavemanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.time.*;

@Configuration
public class DataSeeder {
    @Bean CommandLineRunner seed(UserRepository users,LeaveRequestRepository leaves,PayrollRepository payroll,AttendanceRepository attendance,PasswordEncoder encoder){
        return args->{
            if(users.count()>0)return;
            User admin=user("ADM001","System","Admin","admin@example.com","Admin@123","HR",Role.ADMIN,encoder); User employee=user("EMP001","Jane","Employee","jane@example.com","Employee@123","Engineering",Role.EMPLOYEE,encoder); users.save(admin); users.save(employee);
            LeaveRequest l=new LeaveRequest(); l.setEmployee(employee); l.setLeaveType(LeaveType.PAID); l.setStartDate(LocalDate.now().plusDays(5)); l.setEndDate(LocalDate.now().plusDays(6)); l.setDays(2); l.setRemarks("Personal work"); leaves.save(l);
            PayrollRecord p=new PayrollRecord(); p.setEmployee(employee); p.setPayPeriod(YearMonth.now().toString()); p.setBasicSalary(new BigDecimal("5000.00")); p.setAllowances(new BigDecimal("500.00")); p.setDeductions(new BigDecimal("250.00")); p.setNetSalary(new BigDecimal("5250.00")); p.setUpdatedBy(admin); payroll.save(p);
            AttendanceRecord a=new AttendanceRecord(); a.setEmployee(employee); a.setAttendanceDate(LocalDate.now()); a.setStatus(AttendanceStatus.PRESENT); a.setCheckIn(LocalTime.of(9,0)); a.setCheckOut(LocalTime.of(17,0)); attendance.save(a);
        };
    }
    private User user(String code,String first,String last,String email,String password,String dept,Role role,PasswordEncoder encoder){User u=new User();u.setEmployeeCode(code);u.setFirstName(first);u.setLastName(last);u.setEmail(email);u.setPasswordHash(encoder.encode(password));u.setDepartment(dept);u.setRole(role);return u;}
}
