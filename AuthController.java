package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.service.AuthService;
import jakarta.validation.Valid;
package com.example.employeeattendance.controller;

import com.example.employeeattendance.dto.AuthDtos;
import com.example.employeeattendance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth){this.auth=auth;}
    @PostMapping("/login") public ApiDtos.LoginResponse login(@Valid @RequestBody ApiDtos.LoginRequest request){return auth.login(request);}
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDtos.AuthResponse> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public AuthDtos.AuthResponse login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return authService.login(request);
    }
}
