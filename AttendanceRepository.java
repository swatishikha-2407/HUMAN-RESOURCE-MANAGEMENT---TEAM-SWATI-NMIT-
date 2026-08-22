package com.example.employeeattendance.repository;

import com.example.employeeattendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeEmployeeIdAndDate(String employeeId, LocalDate date);
    List<Attendance> findByEmployeeEmployeeIdAndDateBetweenOrderByDateDesc(String employeeId, LocalDate from, LocalDate to);
    List<Attendance> findByDateBetweenOrderByDateDesc(LocalDate from, LocalDate to);
}
