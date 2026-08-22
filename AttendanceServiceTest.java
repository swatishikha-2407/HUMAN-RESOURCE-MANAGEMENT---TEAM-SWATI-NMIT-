package com.example.employeeattendance;

import com.example.employeeattendance.entity.Attendance;
import com.example.employeeattendance.entity.User;
import com.example.employeeattendance.exception.ApiException;
import com.example.employeeattendance.repository.AttendanceRepository;
import com.example.employeeattendance.service.AttendanceService;
import com.example.employeeattendance.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {
    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    EmployeeService employeeService;

    @InjectMocks
    AttendanceService attendanceService;

    @Test
    void checkInCreatesPresentRecord() {
        User user = new User();
        user.setEmployeeId("EMP101");
        user.setEmail("employee@example.com");
        when(employeeService.getUserByEmail("employee@example.com")).thenReturn(user);
        when(attendanceRepository.findByEmployeeEmployeeIdAndDate("EMP101", LocalDate.now()))
                .thenReturn(Optional.empty());
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = attendanceService.checkIn("employee@example.com");

        assertEquals("EMP101", result.employeeId());
        assertEquals("PRESENT", result.status().name());
        assertNotNull(result.checkIn());
    }

    @Test
    void duplicateCheckInIsRejected() {
        User user = new User();
        user.setEmployeeId("EMP101");
        when(employeeService.getUserByEmail("employee@example.com")).thenReturn(user);
        when(attendanceRepository.findByEmployeeEmployeeIdAndDate("EMP101", LocalDate.now()))
                .thenReturn(Optional.of(new Attendance()));

        assertThrows(ApiException.class, () -> attendanceService.checkIn("employee@example.com"));
    }
}
