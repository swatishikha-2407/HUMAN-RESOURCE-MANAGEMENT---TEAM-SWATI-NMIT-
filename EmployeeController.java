package com.example.employeeattendance.controller;

import com.example.employeeattendance.dto.EmployeeDtos;
import com.example.employeeattendance.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/me")
    public EmployeeDtos.EmployeeResponse current(Authentication authentication) {
        return employeeService.findCurrent(authentication.getName());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PostMapping
    public ResponseEntity<EmployeeDtos.EmployeeResponse> create(
            @Valid @RequestBody EmployeeDtos.CreateEmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.create(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping
    public List<EmployeeDtos.EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @GetMapping("/{employeeId}")
    public EmployeeDtos.EmployeeResponse findOne(@PathVariable String employeeId) {
        return employeeService.findByEmployeeId(employeeId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @PutMapping("/{employeeId}")
    public EmployeeDtos.EmployeeResponse update(@PathVariable String employeeId,
                                                  @Valid @RequestBody EmployeeDtos.UpdateEmployeeRequest request) {
        return employeeService.update(employeeId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String employeeId) {
        employeeService.delete(employeeId);
    }
}
