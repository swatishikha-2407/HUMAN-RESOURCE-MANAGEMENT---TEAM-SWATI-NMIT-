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
}
