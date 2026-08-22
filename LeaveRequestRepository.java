package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
    Optional<LeaveRequest> findByIdAndEmployeeId(Long id, Long employeeId);
    long countByStatus(LeaveStatus status);
    @Query("select coalesce(sum(l.days),0) from LeaveRequest l where l.status=:status") long sumDaysByStatus(@Param("status") LeaveStatus status);
    @Query("select l.leaveType, count(l) from LeaveRequest l group by l.leaveType") List<Object[]> countByType();
}
