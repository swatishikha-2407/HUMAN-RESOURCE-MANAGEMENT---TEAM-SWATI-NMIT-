package com.example.leavemanagement.model;

import jakarta.persistence.*;
import java.time.*;

@Entity
@Table(name="leave_requests")
public class LeaveRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) @JoinColumn(name="employee_id") private User employee;
    @Enumerated(EnumType.STRING) @Column(name="leave_type",nullable=false) private LeaveType leaveType;
    @Column(name="start_date",nullable=false) private LocalDate startDate;
    @Column(name="end_date",nullable=false) private LocalDate endDate;
    @Column(nullable=false) private int days;
    private String remarks;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private LeaveStatus status=LeaveStatus.PENDING;
    @Column(name="admin_comment") private String adminComment;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="reviewed_by") private User reviewedBy;
    @Column(name="reviewed_at") private LocalDateTime reviewedAt;
    @Column(name="created_at",nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public LeaveRequest() {}
    public Long getId(){return id;} public User getEmployee(){return employee;} public void setEmployee(User v){employee=v;} public LeaveType getLeaveType(){return leaveType;} public void setLeaveType(LeaveType v){leaveType=v;}
    public LocalDate getStartDate(){return startDate;} public void setStartDate(LocalDate v){startDate=v;} public LocalDate getEndDate(){return endDate;} public void setEndDate(LocalDate v){endDate=v;} public int getDays(){return days;} public void setDays(int v){days=v;}
    public String getRemarks(){return remarks;} public void setRemarks(String v){remarks=v;} public LeaveStatus getStatus(){return status;} public void setStatus(LeaveStatus v){status=v;} public String getAdminComment(){return adminComment;} public void setAdminComment(String v){adminComment=v;}
    public void setReviewedBy(User v){reviewedBy=v;} public void setReviewedAt(LocalDateTime v){reviewedAt=v;} public LocalDateTime getCreatedAt(){return createdAt;}
}
