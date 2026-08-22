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
package com.example.employee_backend.service;

import com.example.employee_backend.domain.User;
import com.example.employee_backend.domain.RevokedToken;
import com.example.employee_backend.domain.enums.Role;
import com.example.employee_backend.dto.*;
import com.example.employee_backend.exception.BadRequestException;
import com.example.employee_backend.repository.*;
import com.example.employee_backend.security.JwtService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager authManager;
    private final JwtService jwt; private final MailService mail; private final RevokedTokenRepository revoked;
    private final long verificationExpirationMs; private final String adminSignupCode;
    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt,
                       MailService mail, RevokedTokenRepository revoked,
                       @Value("${app.email-verification-expiration-ms}") long verificationExpirationMs,
                       @Value("${app.admin-signup-code:}") String adminSignupCode) {
        this.users=users; this.encoder=encoder; this.authManager=authManager; this.jwt=jwt; this.mail=mail; this.revoked=revoked;
        this.verificationExpirationMs=verificationExpirationMs; this.adminSignupCode=adminSignupCode;
    }
    @Transactional public MessageResponse signup(SignupRequest r) {
        String email=r.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) throw new BadRequestException("Email is already registered.");
        if (users.existsByEmployeeId(r.employeeId().trim())) throw new BadRequestException("Employee ID is already registered.");
        Role role = r.role() == Role.ADMIN && !adminSignupCode.isBlank() && adminSignupCode.equals(r.adminSignupCode()) ? Role.ADMIN : Role.EMPLOYEE;
        User u=new User(); u.setEmployeeId(r.employeeId().trim()); u.setEmail(email); u.setPasswordHash(encoder.encode(r.password())); u.setRole(role);
        u.setFirstName(r.firstName()); u.setLastName(r.lastName()); u.setDepartment(r.department()); u.setPosition(r.position()); u.setPhone(r.phone());
        String token=UUID.randomUUID().toString(); u.setVerificationToken(token); u.setVerificationTokenExpiry(Instant.now().plusMillis(verificationExpirationMs));
        users.save(u); mail.sendVerificationEmail(email, token);
        return new MessageResponse("Signup successful. Verify your email before logging in.");
    }
    public AuthResponse login(LoginRequest r) {
        String email=r.email().trim().toLowerCase();
        User u=users.findByEmailIgnoreCase(email).orElseThrow(() -> new BadRequestException("Invalid email or password."));
        if (!u.isEmailVerified()) throw new BadRequestException("Please verify your email before logging in.");
        try { authManager.authenticate(new UsernamePasswordAuthenticationToken(email, r.password())); }
        catch (AuthenticationException ex) { throw new BadRequestException("Invalid email or password."); }
        return new AuthResponse(jwt.generate(u), "Bearer", jwt.getExpirationMs(), EmployeeResponse.from(u));
    }
    @Transactional public MessageResponse verify(String token) {
        User u=users.findByVerificationToken(token).orElseThrow(() -> new BadRequestException("Invalid verification token."));
        if (u.getVerificationTokenExpiry() == null || u.getVerificationTokenExpiry().isBefore(Instant.now())) throw new BadRequestException("Verification token has expired.");
        u.setEmailVerified(true); u.setVerificationToken(null); u.setVerificationTokenExpiry(null); users.save(u);
        return new MessageResponse("Email verified successfully. You can now log in.");
    }
    public MessageResponse logout(String token) {
        try { revoked.save(new RevokedToken(jwt.getJti(token), jwt.getExpiration(token))); }
        catch (Exception ex) { throw new BadRequestException("Invalid token."); }
        return new MessageResponse("Logged out successfully.");
    }
}
