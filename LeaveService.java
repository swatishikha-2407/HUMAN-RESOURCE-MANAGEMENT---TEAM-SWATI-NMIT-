package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.*;
import com.example.leavemanagement.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeaveService {
    private final LeaveRequestRepository leaves; private final UserRepository users;
    public LeaveService(LeaveRequestRepository leaves,UserRepository users){this.leaves=leaves;this.users=users;}
    @Transactional public ApiDtos.LeaveResponse apply(User employee,ApiDtos.LeaveApplicationRequest r){
        if(r.endDate().isBefore(r.startDate())) throw new IllegalArgumentException("End date must not be before start date");
        LeaveRequest l=new LeaveRequest(); l.setEmployee(employee); l.setLeaveType(r.leaveType()); l.setStartDate(r.startDate()); l.setEndDate(r.endDate()); l.setDays((int)ChronoUnit.DAYS.between(r.startDate(),r.endDate())+1); l.setRemarks(r.remarks());
        return toResponse(leaves.save(l));
    }
    public List<ApiDtos.LeaveResponse> employeeHistory(User u){return leaves.findByEmployeeIdOrderByCreatedAtDesc(u.getId()).stream().map(this::toResponse).toList();}
    public List<ApiDtos.LeaveResponse> all(){return leaves.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();}
    @Transactional public ApiDtos.LeaveResponse review(Long id,User admin,ApiDtos.LeaveReviewRequest r){
        LeaveRequest l=leaves.findById(id).orElseThrow(()->new IllegalArgumentException("Leave request not found"));
        if(l.getStatus()!=LeaveStatus.PENDING) throw new IllegalStateException("Only pending requests can be reviewed");
        l.setStatus(r.status()); l.setAdminComment(r.adminComment()); l.setReviewedBy(admin); l.setReviewedAt(LocalDateTime.now()); return toResponse(leaves.save(l));
    }
    private ApiDtos.LeaveResponse toResponse(LeaveRequest l){return new ApiDtos.LeaveResponse(l.getId(),l.getEmployee().getEmployeeCode(),l.getLeaveType(),l.getStartDate(),l.getEndDate(),l.getDays(),l.getRemarks(),l.getStatus(),l.getAdminComment(),l.getCreatedAt());}
}
