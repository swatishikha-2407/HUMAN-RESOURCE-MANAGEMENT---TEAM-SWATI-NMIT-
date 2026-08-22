package com.example.leavemanagement.repository;

import com.example.leavemanagement.model.PayrollRecord;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.*;

public interface PayrollRepository extends JpaRepository<PayrollRecord, Long> {
    List<PayrollRecord> findByEmployeeIdOrderByPayPeriodDesc(Long employeeId);
    List<PayrollRecord> findAllByOrderByPayPeriodDesc();
    Optional<PayrollRecord> findByEmployeeIdAndPayPeriod(Long employeeId, String payPeriod);
    @Query("select coalesce(sum(p.netSalary),0) from PayrollRecord p where p.payPeriod=:period")
    BigDecimal totalNetSalary(@Param("period") String period);
    @Query("select p.payPeriod, coalesce(sum(p.netSalary),0) from PayrollRecord p group by p.payPeriod order by p.payPeriod")
    List<Object[]> payrollByPeriod();
}
