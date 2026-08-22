package com.example.employeeattendance.service;

import com.example.employeeattendance.dto.AuthDtos;
import com.example.employeeattendance.entity.EmployeeProfile;
import com.example.employeeattendance.entity.Enums;
import com.example.employeeattendance.entity.User;
import com.example.employeeattendance.exception.ApiException;
import com.example.employeeattendance.repository.UserRepository;
import com.example.employeeattendance.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = findByEmail(request.email());
        UserDetails details = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())
                .build();
        return new AuthDtos.AuthResponse(jwtService.generateToken(details), "Bearer",
                user.getEmployeeId(), user.getEmail(), user.getRole());
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        if (userRepository.existsByEmployeeId(request.employeeId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Employee ID is already registered");
        }

        User user = new User();
        user.setEmployeeId(request.employeeId());
        user.setEmail(request.email().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Enums.Role.EMPLOYEE);

        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setName(request.name());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setDepartment(request.department());
        profile.setDesignation(request.designation());
        profile.setJoiningDate(parseDate(request.joiningDate()));
        profile.setProfilePicture(request.profilePicture());
        user.setProfile(profile);

        User saved = userRepository.save(user);
        UserDetails details = org.springframework.security.core.userdetails.User.builder()
                .username(saved.getEmail())
                .password(saved.getPasswordHash())
                .authorities("ROLE_EMPLOYEE")
                .build();
        return new AuthDtos.AuthResponse(jwtService.generateToken(details), "Bearer",
                saved.getEmployeeId(), saved.getEmail(), saved.getRole());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date);
        } catch (RuntimeException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "joiningDate must use yyyy-MM-dd format");
        }
    }
}
