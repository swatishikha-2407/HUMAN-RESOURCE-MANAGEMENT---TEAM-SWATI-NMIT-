package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {
    private final PayrollService payroll; private final AuthService auth;
    public PayrollController(PayrollService payroll,AuthService auth){this.payroll=payroll;this.auth=auth;}
    @GetMapping("/mine") @PreAuthorize("hasRole('EMPLOYEE')") public List<ApiDtos.PayrollResponse> mine(Authentication a){return payroll.employeeView(auth.current(a.getName()));}
    @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<ApiDtos.PayrollResponse> all(){return payroll.adminView();}
    @PutMapping("/employees/{employeeId}") @PreAuthorize("hasRole('ADMIN')") public ApiDtos.PayrollResponse upsert(@PathVariable Long employeeId,@Valid @RequestBody ApiDtos.PayrollRequest r,Authentication a){return payroll.upsert(employeeId,auth.current(a.getName()),r);}
}
