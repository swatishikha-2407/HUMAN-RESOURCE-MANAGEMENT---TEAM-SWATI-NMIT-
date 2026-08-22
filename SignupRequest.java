package com.example.employee_backend.dto;

import com.example.employee_backend.domain.enums.Role;
import jakarta.validation.constraints.*;

public record SignupRequest(
    @NotBlank @Size(max = 50) String employeeId,
    @NotBlank @Email @Size(max = 180) String email,
    @NotBlank @Size(min = 8, max = 72) @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).+$", message = "Password must contain uppercase, lowercase, number, and special character") String password,
    @NotNull Role role,
    @Size(max = 100) String adminSignupCode,
    @Size(max = 100) String firstName,
    @Size(max = 100) String lastName,
    @Size(max = 100) String department,
    @Size(max = 100) String position,
    @Size(max = 30) String phone
) {}
