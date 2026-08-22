package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.User;
import com.example.leavemanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    private final LeaveService leaves; private final AuthService auth;
    public LeaveController(LeaveService leaves,AuthService auth){this.leaves=leaves;this.auth=auth;}
    @PostMapping @PreAuthorize("hasRole('EMPLOYEE')") public ApiDtos.LeaveResponse apply(@Valid @RequestBody ApiDtos.LeaveApplicationRequest r,Authentication a){return leaves.apply(auth.current(a.getName()),r);}
    @GetMapping("/mine") @PreAuthorize("hasRole('EMPLOYEE')") public List<ApiDtos.LeaveResponse> mine(Authentication a){return leaves.employeeHistory(auth.current(a.getName()));}
    @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<ApiDtos.LeaveResponse> all(){return leaves.all();}
    @PatchMapping("/{id}/review") @PreAuthorize("hasRole('ADMIN')") public ApiDtos.LeaveResponse review(@PathVariable Long id,@Valid @RequestBody ApiDtos.LeaveReviewRequest r,Authentication a){return leaves.review(id,auth.current(a.getName()),r);}
}
