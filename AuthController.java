package com.example.leavemanagement.controller;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth){this.auth=auth;}
    @PostMapping("/login") public ApiDtos.LoginResponse login(@Valid @RequestBody ApiDtos.LoginRequest request){return auth.login(request);}
}
