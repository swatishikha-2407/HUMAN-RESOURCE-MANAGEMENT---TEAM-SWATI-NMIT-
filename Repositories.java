package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public interface UserRepository extends JpaRepository<User,Long> { Optional<User> findByEmailIgnoreCase(String email); long countByActiveTrue(); }

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
    Optional<LeaveRequest> findByIdAndEmployeeId(Long id,Long employeeId);
    long countByStatus(LeaveStatus status);
    @Query("select coalesce(sum(l.days),0) from LeaveRequest l where l.status=:status") long sumDaysByStatus(@Param("status") LeaveStatus status);
    @Query("select l.leaveType, count(l) from LeaveRequest l group by l.leaveType") List<Object[]> countByType();
}

public interface PayrollRepository extends JpaRepository<PayrollRecord,Long> {
    List<PayrollRecord> findByEmployeeIdOrderByPayPeriodDesc(Long employeeId);
    List<PayrollRecord> findAllByOrderByPayPeriodDesc();
    Optional<PayrollRecord> findByEmployeeIdAndPayPeriod(Long employeeId,String payPeriod);
    @Query("select coalesce(sum(p.netSalary),0) from PayrollRecord p where p.payPeriod=:period") BigDecimal totalNetSalary(@Param("period") String period);
    @Query("select p.payPeriod, coalesce(sum(p.netSalary),0) from PayrollRecord p group by p.payPeriod order by p.payPeriod") List<Object[]> payrollByPeriod();
}

public interface AttendanceRepository extends JpaRepository<AttendanceRecord,Long> {
    List<AttendanceRecord> findByEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(Long id,LocalDate from,LocalDate to);
    Optional<AttendanceRecord> findByEmployeeIdAndAttendanceDate(Long id,LocalDate date);
    @Query("select a.status, count(a) from AttendanceRecord a where a.attendanceDate between :from and :to group by a.status") List<Object[]> countByStatus(@Param("from") LocalDate from,@Param("to") LocalDate to);
    @Query("select a.attendanceDate, a.status, count(a) from AttendanceRecord a where a.attendanceDate between :from and :to group by a.attendanceDate, a.status order by a.attendanceDate") List<Object[]> dailyStatus(@Param("from") LocalDate from,@Param("to") LocalDate to);
}
