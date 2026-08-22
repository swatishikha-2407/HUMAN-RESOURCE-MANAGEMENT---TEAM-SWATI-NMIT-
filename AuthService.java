package com.example.leavemanagement.service;

import com.example.leavemanagement.dto.ApiDtos;
import com.example.leavemanagement.model.User;
import com.example.leavemanagement.repository.UserRepository;
import com.example.leavemanagement.security.JwtService;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final JwtService jwt; private final AuthenticationManager authenticationManager;
    public AuthService(UserRepository users,PasswordEncoder encoder,JwtService jwt,AuthenticationManager authenticationManager){this.users=users;this.encoder=encoder;this.jwt=jwt;this.authenticationManager=authenticationManager;}
    public ApiDtos.LoginResponse login(ApiDtos.LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(),request.password()));
        User u=users.findByEmailIgnoreCase(request.email()).orElseThrow();
        return new ApiDtos.LoginResponse(jwt.createToken(u.getEmail(),u.getRole().name()),u.getId(),u.getEmail(),u.getRole());
    }
    public User current(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new IllegalArgumentException("Authenticated user not found"));}
}
