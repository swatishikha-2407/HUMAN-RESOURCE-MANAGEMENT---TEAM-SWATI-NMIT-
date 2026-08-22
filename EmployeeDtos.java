package com.example.employeeattendance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class EmployeeDtos {
    private EmployeeDtos() {}

    public record CreateEmployeeRequest(
            @NotBlank @Size(max = 30) String employeeId,
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 30) String phone,
            @NotBlank @Size(max = 500) String address,
            @NotBlank @Size(max = 100) String department,
            @NotBlank @Size(max = 100) String designation,
            @NotNull String joiningDate,
            @Size(max = 500) String profilePicture
    ) {}

    public record UpdateEmployeeRequest(
            @NotBlank @Email @Size(max = 180) String email,
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 30) String phone,
            @NotBlank @Size(max = 500) String address,
            @NotBlank @Size(max = 100) String department,
            @NotBlank @Size(max = 100) String designation,
            @NotNull String joiningDate,
            @Size(max = 500) String profilePicture
    ) {}

    public record EmployeeResponse(
            Long id,
            String employeeId,
            String email,
            String role,
            String name,
            String phone,
            String address,
            String department,
            String designation,
            String joiningDate,
            String profilePicture
    ) {}
}
