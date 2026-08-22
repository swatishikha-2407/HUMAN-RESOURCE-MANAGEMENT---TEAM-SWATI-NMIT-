package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class PayrollService {
    private final PayrollRepository payroll; private final UserRepository users;
    public PayrollService(PayrollRepository payroll,UserRepository users){this.payroll=payroll;this.users=users;}
    public List<ApiDtos.PayrollResponse> employeeView(User u){return payroll.findByEmployeeIdOrderByPayPeriodDesc(u.getId()).stream().map(this::toResponse).toList();}
    public List<ApiDtos.PayrollResponse> adminView(){return payroll.findAllByOrderByPayPeriodDesc().stream().map(this::toResponse).toList();}
    @Transactional public ApiDtos.PayrollResponse upsert(Long employeeId,User admin,ApiDtos.PayrollRequest r){
        User employee=users.findById(employeeId).orElseThrow(()->new IllegalArgumentException("Employee not found"));
        PayrollRecord p=payroll.findByEmployeeIdAndPayPeriod(employeeId,r.payPeriod()).orElseGet(PayrollRecord::new);
        p.setEmployee(employee); p.setPayPeriod(r.payPeriod()); p.setBasicSalary(r.basicSalary()); p.setAllowances(r.allowances()); p.setDeductions(r.deductions()); p.setNetSalary(r.basicSalary().add(r.allowances()).subtract(r.deductions())); p.setUpdatedBy(admin);
        return toResponse(payroll.save(p));
    }
    private ApiDtos.PayrollResponse toResponse(PayrollRecord p){return new ApiDtos.PayrollResponse(p.getId(),p.getEmployee().getEmployeeCode(),p.getPayPeriod(),p.getBasicSalary(),p.getAllowances(),p.getDeductions(),p.getNetSalary());}
}
