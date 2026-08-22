package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<AttendanceRecord, Long> {
    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(Long id, LocalDate from, LocalDate to);
    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(Long id, LocalDate date);
    @Query("select a.status, count(a) from AttendanceRecord a where a.attendanceDate between :from and :to group by a.status")
    List<Object[]> countByStatus(@Param("from") LocalDate from, @Param("to") LocalDate to);
    @Query("select a.attendanceDate, a.status, count(a) from AttendanceRecord a where a.attendanceDate between :from and :to group by a.attendanceDate, a.status order by a.attendanceDate")
    List<Object[]> dailyStatus(@Param("from") LocalDate from, @Param("to") LocalDate to);
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
