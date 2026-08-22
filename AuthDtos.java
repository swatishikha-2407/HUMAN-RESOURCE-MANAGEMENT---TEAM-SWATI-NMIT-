package com.example.employeeattendance.dto;

import com.example.employeeattendance.entity.Enums;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record RegisterRequest(
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

    public record AuthResponse(
            String token,
            String tokenType,
            String employeeId,
            String email,
            Enums.Role role
    ) {}
}
