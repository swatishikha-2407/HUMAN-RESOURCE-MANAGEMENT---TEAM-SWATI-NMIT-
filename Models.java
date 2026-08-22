package com.example.leavemanagement.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.*;

public final class Models {
    private Models() {}
}

enum Role { EMPLOYEE, ADMIN }
enum LeaveType { PAID, SICK, UNPAID }
enum LeaveStatus { PENDING, APPROVED, REJECTED }
enum AttendanceStatus { PRESENT, ABSENT, LEAVE }

@Entity
@Table(name = "users")
class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="employee_code", nullable=false, unique=true) private String employeeCode;
    @Column(name="first_name", nullable=false) private String firstName;
    @Column(name="last_name", nullable=false) private String lastName;
    @Column(nullable=false, unique=true) private String email;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Column(nullable=false) private String department;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role = Role.EMPLOYEE;
    @Column(nullable=false) private boolean active = true;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt = LocalDateTime.now();
    public Long getId(){return id;} public String getEmployeeCode(){return employeeCode;} public void setEmployeeCode(String v){employeeCode=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;} public Role getRole(){return role;} public void setRole(Role v){role=v;} public boolean isActive(){return active;}
}

@Entity
@Table(name="leave_requests")
class LeaveRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="employee_id") private User employee;
    @Enumerated(EnumType.STRING) @Column(name="leave_type",nullable=false) private LeaveType leaveType;
    @Column(name="start_date",nullable=false) private LocalDate startDate; @Column(name="end_date",nullable=false) private LocalDate endDate;
    @Column(nullable=false) private int days; private String remarks;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LeaveStatus status=LeaveStatus.PENDING;
    @Column(name="admin_comment") private String adminComment;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reviewed_by") private User reviewedBy;
    @Column(name="reviewed_at") private LocalDateTime reviewedAt; @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public User getEmployee(){return employee;} public void setEmployee(User v){employee=v;} public LeaveType getLeaveType(){return leaveType;} public void setLeaveType(LeaveType v){leaveType=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public int getDays(){return days;} public void setDays(int v){days=v;}
    public String getRemarks(){return remarks;} public void setRemarks(String v){remarks=v;} public LeaveStatus getStatus(){return status;} public void setStatus(LeaveStatus v){status=v;} public String getAdminComment(){return adminComment;} public void setAdminComment(String v){adminComment=v;}
    public void setReviewedBy(User v){reviewedBy=v;} public LocalDateTime getReviewedAt(){return reviewedAt;} public void setReviewedAt(LocalDateTime v){reviewedAt=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}

@Entity
@Table(name="payroll_records", uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","pay_period"}))
class PayrollRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id") private User employee;
    @Column(name="pay_period",nullable=false) private String payPeriod; @Column(name="basic_salary",nullable=false) private BigDecimal basicSalary;
    @Column(nullable=false) private BigDecimal allowances=BigDecimal.ZERO; @Column(nullable=false) private BigDecimal deductions=BigDecimal.ZERO;
    @Column(name="net_salary",nullable=false) private BigDecimal netSalary; @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="updated_by") private User updatedBy;
    public Long getId(){return id;} public User getEmployee(){return employee;} public void setEmployee(User v){employee=v;} public String getPayPeriod(){return payPeriod;} public void setPayPeriod(String v){payPeriod=v;}
    public BigDecimal getBasicSalary(){return basicSalary;} public void setBasicSalary(BigDecimal v){basicSalary=v;} public BigDecimal getAllowances(){return allowances;} public void setAllowances(BigDecimal v){allowances=v;} public BigDecimal getDeductions(){return deductions;} public void setDeductions(BigDecimal v){deductions=v;} public BigDecimal getNetSalary(){return netSalary;} public void setNetSalary(BigDecimal v){netSalary=v;} public void setUpdatedBy(User v){updatedBy=v;}
}

@Entity
@Table(name="attendance_records",uniqueConstraints=@UniqueConstraint(columnNames={"employee_id","attendance_date"}))
class AttendanceRecord {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="employee_id") private User employee;
    @Column(name="attendance_date",nullable=false) private LocalDate attendanceDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private AttendanceStatus status;
    @Column(name="check_in") private LocalTime checkIn; @Column(name="check_out") private LocalTime checkOut; private String notes;
    public Long getId(){return id;} public User getEmployee(){return employee;} public void setEmployee(User v){employee=v;} public LocalDate getAttendanceDate(){return attendanceDate;} public void setAttendanceDate(LocalDate v){attendanceDate=v;} public AttendanceStatus getStatus(){return status;} public void setStatus(AttendanceStatus v){status=v;} public LocalTime getCheckIn(){return checkIn;} public void setCheckIn(LocalTime v){checkIn=v;} public LocalTime getCheckOut(){return checkOut;} public void setCheckOut(LocalTime v){checkOut=v;} public void setNotes(String v){notes=v;}
}
