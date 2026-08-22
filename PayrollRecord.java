package com.example.leavemanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="payroll_records", uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","pay_period"}))
public class PayrollRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id") private User employee;
    @Column(name="pay_period",nullable=false) private String payPeriod;
    @Column(name="basic_salary",nullable=false) private BigDecimal basicSalary;
    @Column(nullable=false) private BigDecimal allowances=BigDecimal.ZERO;
    @Column(nullable=false) private BigDecimal deductions=BigDecimal.ZERO;
    @Column(name="net_salary",nullable=false) private BigDecimal netSalary;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="updated_by") private User updatedBy;
    public PayrollRecord() {}
    public Long getId(){return id;} public User getEmployee(){return employee;} public void setEmployee(User v){employee=v;} public String getPayPeriod(){return payPeriod;} public void setPayPeriod(String v){payPeriod=v;}
    public BigDecimal getBasicSalary(){return basicSalary;} public void setBasicSalary(BigDecimal v){basicSalary=v;} public BigDecimal getAllowances(){return allowances;} public void setAllowances(BigDecimal v){allowances=v;} public BigDecimal getDeductions(){return deductions;} public void setDeductions(BigDecimal v){deductions=v;} public BigDecimal getNetSalary(){return netSalary;} public void setNetSalary(BigDecimal v){netSalary=v;} public void setUpdatedBy(User v){updatedBy=v;}
}
