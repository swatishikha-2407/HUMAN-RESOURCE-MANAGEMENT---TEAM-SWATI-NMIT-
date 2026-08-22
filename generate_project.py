from pathlib import Path

root = Path('/home/ubuntu/employee-backend/src/main/java/com/example/employee_backend')
files = {}

def add(rel, content):
    files[rel] = content.strip() + '\n'

add('EmployeeBackendApplication.java', r'''
package com.example.employee_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmployeeBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmployeeBackendApplication.class, args);
    }
}
''')

add('domain/enums/Role.java', r'''
package com.example.employee_backend.domain.enums;

public enum Role {
    EMPLOYEE,
    ADMIN
}
''')
add('domain/enums/LeaveStatus.java', r'''
package com.example.employee_backend.domain.enums;

public enum LeaveStatus {
    PENDING,
    APPROVED,
    REJECTED
}
''')

add('domain/User.java', r'''
package com.example.employee_backend.domain;

import com.example.employee_backend.domain.enums.Role;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String employeeId;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.EMPLOYEE;

    @Column(nullable = false)
    private boolean emailVerified = false;

    @Column(nullable = false)
    private boolean active = true;

    private String firstName;
    private String lastName;
    private String department;
    private String position;
    private String phone;

    @Column(length = 120)
    private String verificationToken;
    private Instant verificationTokenExpiry;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getVerificationToken() { return verificationToken; }
    public void setVerificationToken(String verificationToken) { this.verificationToken = verificationToken; }
    public Instant getVerificationTokenExpiry() { return verificationTokenExpiry; }
    public void setVerificationTokenExpiry(Instant verificationTokenExpiry) { this.verificationTokenExpiry = verificationTokenExpiry; }
    public Instant getCreatedAt() { return createdAt; }
}
''')

add('domain/Attendance.java', r'''
package com.example.employee_backend.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "attendance_date"}))
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "attendance_date", nullable = false)
    private LocalDate date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public LocalDateTime getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDateTime checkIn) { this.checkIn = checkIn; }
    public LocalDateTime getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDateTime checkOut) { this.checkOut = checkOut; }
}
''')

add('domain/LeaveRequest.java', r'''
package com.example.employee_backend.domain;

import com.example.employee_backend.domain.enums.LeaveStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false) private LocalDate startDate;
    @Column(nullable = false) private LocalDate endDate;
    @Column(nullable = false, length = 1000) private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private LeaveStatus status = LeaveStatus.PENDING;
    @Column(nullable = false, updatable = false) private Instant createdAt;
    private Instant reviewedAt;

    @PrePersist void onCreate() { if (createdAt == null) createdAt = Instant.now(); }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }
}
''')

add('domain/Payroll.java', r'''
package com.example.employee_backend.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payroll", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "pay_year", "pay_month"}))
public class Payroll {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(name = "pay_year", nullable = false) private int year;
    @Column(name = "pay_month", nullable = false) private int month;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal basicSalary;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal allowances = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal deductions = BigDecimal.ZERO;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal netSalary;
    @Column(nullable = false) private boolean paid = false;
    @Column(nullable = false) private Instant updatedAt;

    @PrePersist @PreUpdate void calculate() {
        if (allowances == null) allowances = BigDecimal.ZERO;
        if (deductions == null) deductions = BigDecimal.ZERO;
        if (basicSalary == null) basicSalary = BigDecimal.ZERO;
        netSalary = basicSalary.add(allowances).subtract(deductions);
        updatedAt = Instant.now();
    }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal v) { this.basicSalary = v; }
    public BigDecimal getAllowances() { return allowances; }
    public void setAllowances(BigDecimal v) { this.allowances = v; }
    public BigDecimal getDeductions() { return deductions; }
    public void setDeductions(BigDecimal v) { this.deductions = v; }
    public BigDecimal getNetSalary() { return netSalary; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }
    public Instant getUpdatedAt() { return updatedAt; }
}
''')

add('domain/RevokedToken.java', r'''
package com.example.employee_backend.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "revoked_tokens", indexes = @Index(name = "idx_revoked_jti", columnList = "jti", unique = true))
public class RevokedToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100) private String jti;
    @Column(nullable = false) private Instant expiresAt;
    protected RevokedToken() {}
    public RevokedToken(String jti, Instant expiresAt) { this.jti = jti; this.expiresAt = expiresAt; }
    public String getJti() { return jti; }
    public Instant getExpiresAt() { return expiresAt; }
}
''')

add('repository/UserRepository.java', r'''
package com.example.employee_backend.repository;

import com.example.employee_backend.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    Optional<User> findByEmployeeId(String employeeId);
    Optional<User> findByVerificationToken(String token);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmployeeId(String employeeId);
}
''')
add('repository/AttendanceRepository.java', r'''
package com.example.employee_backend.repository;

import com.example.employee_backend.domain.Attendance;
import com.example.employee_backend.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByUserAndDate(User user, LocalDate date);
    List<Attendance> findByUserOrderByDateDesc(User user);
    List<Attendance> findAllByOrderByDateDesc();
}
''')
add('repository/LeaveRequestRepository.java', r'''
package com.example.employee_backend.repository;

import com.example.employee_backend.domain.LeaveRequest;
import com.example.employee_backend.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByUserOrderByCreatedAtDesc(User user);
    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
}
''')
add('repository/PayrollRepository.java', r'''
package com.example.employee_backend.repository;

import com.example.employee_backend.domain.Payroll;
import com.example.employee_backend.domain.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {
    List<Payroll> findByUserOrderByYearDescMonthDesc(User user);
    List<Payroll> findAllByOrderByYearDescMonthDesc();
}
''')
add('repository/RevokedTokenRepository.java', r'''
package com.example.employee_backend.repository;

import com.example.employee_backend.domain.RevokedToken;
import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByJti(String jti);
    long deleteByExpiresAtBefore(Instant time);
}
''')

add('dto/SignupRequest.java', r'''
package com.example.employee_backend.dto;

import com.example.employee_backend.domain.enums.Role;
import jakarta.validation.constraints.*;

public record SignupRequest(
    @NotBlank @Size(max = 50) String employeeId,
    @NotBlank @Email @Size(max = 180) String email,
    @NotBlank @Size(min = 8, max = 72) String password,
    @NotNull Role role,
    @Size(max = 100) String adminSignupCode,
    @Size(max = 100) String firstName,
    @Size(max = 100) String lastName,
    @Size(max = 100) String department,
    @Size(max = 100) String position,
    @Size(max = 30) String phone
) {}
''')
add('dto/LoginRequest.java', r'''
package com.example.employee_backend.dto;

import jakarta.validation.constraints.*;

public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
''')
add('dto/AuthResponse.java', r'''
package com.example.employee_backend.dto;

public record AuthResponse(String token, String tokenType, long expiresIn, EmployeeResponse user) {}
''')
add('dto/MessageResponse.java', r'''
package com.example.employee_backend.dto;

public record MessageResponse(String message) {}
''')
add('dto/EmployeeResponse.java', r'''
package com.example.employee_backend.dto;

import com.example.employee_backend.domain.User;
import com.example.employee_backend.domain.enums.Role;
import java.time.Instant;

public record EmployeeResponse(Long id, String employeeId, String email, Role role,
                               boolean emailVerified, boolean active, String firstName,
                               String lastName, String department, String position,
                               String phone, Instant createdAt) {
    public static EmployeeResponse from(User u) {
        return new EmployeeResponse(u.getId(), u.getEmployeeId(), u.getEmail(), u.getRole(),
            u.isEmailVerified(), u.isActive(), u.getFirstName(), u.getLastName(),
            u.getDepartment(), u.getPosition(), u.getPhone(), u.getCreatedAt());
    }
}
''')
add('dto/EmployeeUpdateRequest.java', r'''
package com.example.employee_backend.dto;

import jakarta.validation.constraints.Size;

public record EmployeeUpdateRequest(
    @Size(max = 100) String firstName,
    @Size(max = 100) String lastName,
    @Size(max = 100) String department,
    @Size(max = 100) String position,
    @Size(max = 30) String phone,
    Boolean active
) {}
''')
add('dto/AttendanceResponse.java', r'''
package com.example.employee_backend.dto;

import com.example.employee_backend.domain.Attendance;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AttendanceResponse(Long id, Long employeeUserId, String employeeId,
                                 LocalDate date, LocalDateTime checkIn, LocalDateTime checkOut) {
    public static AttendanceResponse from(Attendance a) {
        return new AttendanceResponse(a.getId(), a.getUser().getId(), a.getUser().getEmployeeId(),
            a.getDate(), a.getCheckIn(), a.getCheckOut());
    }
}
''')
add('dto/LeaveCreateRequest.java', r'''
package com.example.employee_backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record LeaveCreateRequest(@NotNull LocalDate startDate, @NotNull LocalDate endDate,
                                 @NotBlank @Size(max = 1000) String reason) {}
''')
add('dto/LeaveResponse.java', r'''
package com.example.employee_backend.dto;

import com.example.employee_backend.domain.LeaveRequest;
import com.example.employee_backend.domain.enums.LeaveStatus;
import java.time.Instant;
import java.time.LocalDate;

public record LeaveResponse(Long id, Long employeeUserId, String employeeId, LocalDate startDate,
                            LocalDate endDate, String reason, LeaveStatus status,
                            Instant createdAt, Instant reviewedAt) {
    public static LeaveResponse from(LeaveRequest l) {
        return new LeaveResponse(l.getId(), l.getUser().getId(), l.getUser().getEmployeeId(),
            l.getStartDate(), l.getEndDate(), l.getReason(), l.getStatus(), l.getCreatedAt(), l.getReviewedAt());
    }
}
''')
add('dto/PayrollUpsertRequest.java', r'''
package com.example.employee_backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PayrollUpsertRequest(@NotNull Long employeeUserId, @Min(2000) int year,
                                   @Min(1) @Max(12) int month,
                                   @NotNull @DecimalMin("0.00") BigDecimal basicSalary,
                                   @DecimalMin("0.00") BigDecimal allowances,
                                   @DecimalMin("0.00") BigDecimal deductions,
                                   boolean paid) {}
''')
add('dto/PayrollUpdateRequest.java', r'''
package com.example.employee_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record PayrollUpdateRequest(@DecimalMin("0.00") BigDecimal basicSalary,
                                   @DecimalMin("0.00") BigDecimal allowances,
                                   @DecimalMin("0.00") BigDecimal deductions,
                                   Boolean paid) {}
''')
add('dto/PayrollResponse.java', r'''
package com.example.employee_backend.dto;

import com.example.employee_backend.domain.Payroll;
import java.math.BigDecimal;
import java.time.Instant;

public record PayrollResponse(Long id, Long employeeUserId, String employeeId, int year, int month,
                              BigDecimal basicSalary, BigDecimal allowances, BigDecimal deductions,
                              BigDecimal netSalary, boolean paid, Instant updatedAt) {
    public static PayrollResponse from(Payroll p) {
        return new PayrollResponse(p.getId(), p.getUser().getId(), p.getUser().getEmployeeId(),
            p.getYear(), p.getMonth(), p.getBasicSalary(), p.getAllowances(), p.getDeductions(),
            p.getNetSalary(), p.isPaid(), p.getUpdatedAt());
    }
}
''')
add('dto/ApiError.java', r'''
package com.example.employee_backend.dto;

import java.time.Instant;
import java.util.Map;

public record ApiError(Instant timestamp, int status, String error, String message, Map<String, String> validationErrors) {
    public ApiError(int status, String error, String message) { this(Instant.now(), status, error, message, null); }
}
''')

add('exception/ResourceNotFoundException.java', r'''
package com.example.employee_backend.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}
''')
add('exception/BadRequestException.java', r'''
package com.example.employee_backend.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
''')
add('exception/GlobalExceptionHandler.java', r'''
package com.example.employee_backend.exception;

import com.example.employee_backend.dto.ApiError;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> notFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiError(404, "Not Found", ex.getMessage()));
    }
    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<ApiError> badRequest(BadRequestException ex) {
        return ResponseEntity.badRequest().body(new ApiError(400, "Bad Request", ex.getMessage()));
    }
    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ApiError> denied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiError(403, "Forbidden", "You do not have permission to use this resource."));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors()) errors.put(e.getField(), e.getDefaultMessage());
        return ResponseEntity.badRequest().body(new ApiError(java.time.Instant.now(), 400, "Validation Error", "Request validation failed.", errors));
    }
    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> generic(Exception ex) {
        return ResponseEntity.status(500).body(new ApiError(500, "Internal Server Error", "An unexpected error occurred."));
    }
}
''')

add('security/JwtService.java', r'''
package com.example.employee_backend.security;

import com.example.employee_backend.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMs;
    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
        if (secret.length() < 32) throw new IllegalArgumentException("JWT secret must be at least 32 characters.");
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }
    public String generate(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder().subject(user.getEmail()).id(UUID.randomUUID().toString())
            .claim("userId", user.getId()).claim("role", user.getRole().name())
            .issuedAt(now).expiration(expiry).signWith(key).compact();
    }
    public Claims parse(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
    public String getSubject(String token) { return parse(token).getSubject(); }
    public String getJti(String token) { return parse(token).getId(); }
    public Instant getExpiration(String token) { return parse(token).getExpiration().toInstant(); }
    public long getExpirationMs() { return expirationMs; }
}
''')
add('security/CustomUserDetailsService.java', r'''
package com.example.employee_backend.security;

import com.example.employee_backend.domain.User;
import com.example.employee_backend.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u = users.findByEmailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(u.getEmail()).password(u.getPasswordHash())
            .roles(u.getRole().name()).disabled(!u.isActive()).build();
    }
}
''')
add('security/JwtAuthenticationFilter.java', r'''
package com.example.employee_backend.security;

import com.example.employee_backend.repository.RevokedTokenRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final CustomUserDetailsService details;
    private final RevokedTokenRepository revoked;
    public JwtAuthenticationFilter(JwtService jwt, CustomUserDetailsService details, RevokedTokenRepository revoked) {
        this.jwt = jwt; this.details = details; this.revoked = revoked;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            String token = header.substring(7);
            try {
                String jti = jwt.getJti(token);
                if (!revoked.existsByJti(jti)) {
                    String email = jwt.getSubject(token);
                    UserDetails user = details.loadUserByUsername(email);
                    if (user.isEnabled()) {
                        var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // Invalid credentials remain unauthenticated and are rejected by Spring Security.
            }
        }
        chain.doFilter(request, response);
    }
}
''')
add('config/SecurityConfig.java', r'''
package com.example.employee_backend.config;

import com.example.employee_backend.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration @EnableWebSecurity @EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtFilter;
    public SecurityConfig(JwtAuthenticationFilter jwtFilter) { this.jwtFilter = jwtFilter; }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a.requestMatchers("/auth/**", "/h2-console/**", "/error").permitAll().anyRequest().authenticated())
            .exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex) -> res.sendError(HttpStatus.UNAUTHORIZED.value(), "Authentication required")))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
''')

add('service/MailService.java', r'''
package com.example.employee_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender sender;
    private final boolean enabled;
    private final String frontendUrl;
    public MailService(JavaMailSender sender, @Value("${app.mail.enabled:false}") boolean enabled,
                       @Value("${app.frontend-url}") String frontendUrl) {
        this.sender = sender; this.enabled = enabled; this.frontendUrl = frontendUrl;
    }
    public void sendVerificationEmail(String email, String token) {
        String link = frontendUrl + "/verify-email?token=" + token;
        if (!enabled) { System.out.println("EMAIL VERIFICATION LINK for " + email + ": " + link); return; }
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email); helper.setSubject("Verify your employee account");
            helper.setText("Click this link to verify your account: " + link);
            sender.send(message);
        } catch (MessagingException ex) { throw new IllegalStateException("Unable to send verification email", ex); }
    }
}
''')
add('service/AuthService.java', r'''
package com.example.employee_backend.service;

import com.example.employee_backend.domain.User;
import com.example.employee_backend.domain.enums.Role;
import com.example.employee_backend.dto.*;
import com.example.employee_backend.exception.BadRequestException;
import com.example.employee_backend.repository.*;
import com.example.employee_backend.security.JwtService;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
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
''')

add('service/EmployeeService.java', r'''
package com.example.employee_backend.service;

import com.example.employee_backend.domain.User;
import com.example.employee_backend.dto.*;
import com.example.employee_backend.exception.ResourceNotFoundException;
import com.example.employee_backend.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private final UserRepository users;
    public EmployeeService(UserRepository users) { this.users=users; }
    public User getEntity(Long id) { return users.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found.")); }
    public User current(String email) { return users.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("Employee not found.")); }
    public List<EmployeeResponse> all() { return users.findAll().stream().map(EmployeeResponse::from).toList(); }
    public EmployeeResponse byId(Long id) { return EmployeeResponse.from(getEntity(id)); }
    @Transactional public EmployeeResponse update(Long id, EmployeeUpdateRequest r) {
        User u=getEntity(id); if (r.firstName()!=null) u.setFirstName(r.firstName()); if (r.lastName()!=null) u.setLastName(r.lastName());
        if (r.department()!=null) u.setDepartment(r.department()); if (r.position()!=null) u.setPosition(r.position());
        if (r.phone()!=null) u.setPhone(r.phone()); if (r.active()!=null) u.setActive(r.active()); return EmployeeResponse.from(users.save(u));
    }
}
''')
add('service/AttendanceService.java', r'''
package com.example.employee_backend.service;

import com.example.employee_backend.domain.*;
import com.example.employee_backend.dto.AttendanceResponse;
import com.example.employee_backend.exception.BadRequestException;
import com.example.employee_backend.repository.AttendanceRepository;
import java.time.*;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceService {
    private final AttendanceRepository attendance; private final EmployeeService employees;
    public AttendanceService(AttendanceRepository attendance, EmployeeService employees) { this.attendance=attendance; this.employees=employees; }
    @Transactional public AttendanceResponse checkIn(String email) {
        User u=employees.current(email); LocalDate date=LocalDate.now();
        Attendance a=attendance.findByUserAndDate(u,date).orElseGet(() -> { Attendance n=new Attendance(); n.setUser(u); n.setDate(date); return n; });
        if (a.getCheckIn()!=null) throw new BadRequestException("You have already checked in today.");
        a.setCheckIn(LocalDateTime.now()); return AttendanceResponse.from(attendance.save(a));
    }
    @Transactional public AttendanceResponse checkOut(String email) {
        User u=employees.current(email); Attendance a=attendance.findByUserAndDate(u,LocalDate.now()).orElseThrow(() -> new BadRequestException("Check in before checking out."));
        if (a.getCheckIn()==null) throw new BadRequestException("Check in before checking out."); if (a.getCheckOut()!=null) throw new BadRequestException("You have already checked out today.");
        a.setCheckOut(LocalDateTime.now()); return AttendanceResponse.from(attendance.save(a));
    }
    public List<AttendanceResponse> mine(String email) { return attendance.findByUserOrderByDateDesc(employees.current(email)).stream().map(AttendanceResponse::from).toList(); }
    public List<AttendanceResponse> all() { return attendance.findAllByOrderByDateDesc().stream().map(AttendanceResponse::from).toList(); }
}
''')
add('service/LeaveService.java', r'''
package com.example.employee_backend.service;

import com.example.employee_backend.domain.*;
import com.example.employee_backend.domain.enums.LeaveStatus;
import com.example.employee_backend.dto.*;
import com.example.employee_backend.exception.*;
import com.example.employee_backend.repository.LeaveRequestRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaveService {
    private final LeaveRequestRepository leaves; private final EmployeeService employees;
    public LeaveService(LeaveRequestRepository leaves, EmployeeService employees) { this.leaves=leaves; this.employees=employees; }
    @Transactional public LeaveResponse create(String email, LeaveCreateRequest r) {
        if (r.endDate().isBefore(r.startDate())) throw new BadRequestException("End date cannot be before start date.");
        LeaveRequest l=new LeaveRequest(); l.setUser(employees.current(email)); l.setStartDate(r.startDate()); l.setEndDate(r.endDate()); l.setReason(r.reason()); return LeaveResponse.from(leaves.save(l));
    }
    public List<LeaveResponse> mine(String email) { return leaves.findByUserOrderByCreatedAtDesc(employees.current(email)).stream().map(LeaveResponse::from).toList(); }
    public List<LeaveResponse> all() { return leaves.findAllByOrderByCreatedAtDesc().stream().map(LeaveResponse::from).toList(); }
    @Transactional public LeaveResponse decide(Long id, boolean approve) {
        LeaveRequest l=leaves.findById(id).orElseThrow(() -> new ResourceNotFoundException("Leave request not found."));
        if (l.getStatus()!=LeaveStatus.PENDING) throw new BadRequestException("This leave request has already been decided.");
        l.setStatus(approve ? LeaveStatus.APPROVED : LeaveStatus.REJECTED); l.setReviewedAt(Instant.now()); return LeaveResponse.from(leaves.save(l));
    }
}
''')
add('service/PayrollService.java', r'''
package com.example.employee_backend.service;

import com.example.employee_backend.domain.Payroll;
import com.example.employee_backend.dto.*;
import com.example.employee_backend.exception.ResourceNotFoundException;
import com.example.employee_backend.repository.PayrollRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PayrollService {
    private final PayrollRepository payroll; private final EmployeeService employees;
    public PayrollService(PayrollRepository payroll, EmployeeService employees) { this.payroll=payroll; this.employees=employees; }
    public List<PayrollResponse> mine(String email) { return payroll.findByUserOrderByYearDescMonthDesc(employees.current(email)).stream().map(PayrollResponse::from).toList(); }
    public List<PayrollResponse> all() { return payroll.findAllByOrderByYearDescMonthDesc().stream().map(PayrollResponse::from).toList(); }
    @Transactional public PayrollResponse create(PayrollUpsertRequest r) {
        Payroll p=new Payroll(); p.setUser(employees.getEntity(r.employeeUserId())); p.setYear(r.year()); p.setMonth(r.month()); p.setBasicSalary(r.basicSalary());
        p.setAllowances(r.allowances()==null?BigDecimal.ZERO:r.allowances()); p.setDeductions(r.deductions()==null?BigDecimal.ZERO:r.deductions()); p.setPaid(r.paid()); return PayrollResponse.from(payroll.save(p));
    }
    @Transactional public PayrollResponse update(Long id, PayrollUpdateRequest r) {
        Payroll p=payroll.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payroll record not found."));
        if (r.basicSalary()!=null) p.setBasicSalary(r.basicSalary()); if (r.allowances()!=null) p.setAllowances(r.allowances()); if (r.deductions()!=null) p.setDeductions(r.deductions()); if (r.paid()!=null) p.setPaid(r.paid());
        return PayrollResponse.from(payroll.save(p));
    }
}
''')

add('config/ApplicationConfig.java', r'''
package com.example.employee_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class ApplicationConfig {
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception { return config.getAuthenticationManager(); }
}
''')
add('config/DataInitializer.java', r'''
package com.example.employee_backend.config;

import com.example.employee_backend.domain.User;
import com.example.employee_backend.domain.enums.Role;
import com.example.employee_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder,
                                      @Value("${app.seed-admin-email:admin@example.com}") String email,
                                      @Value("${app.seed-admin-password:Admin@12345}") String password) {
        return args -> {
            if (!users.existsByEmailIgnoreCase(email)) {
                User admin = new User(); admin.setEmployeeId("ADMIN-001"); admin.setEmail(email.toLowerCase());
                admin.setPasswordHash(encoder.encode(password)); admin.setRole(Role.ADMIN); admin.setEmailVerified(true);
                admin.setFirstName("System"); admin.setLastName("Administrator"); users.save(admin);
                System.out.println("Seeded admin account: " + email + " (change the password before deployment)");
            }
        };
    }
}
''')

add('controller/AuthController.java', r'''
package com.example.employee_backend.controller;

import com.example.employee_backend.dto.*;
import com.example.employee_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth=auth; }
    @PostMapping("/signup") public ResponseEntity<MessageResponse> signup(@Valid @RequestBody SignupRequest r) { return ResponseEntity.ok(auth.signup(r)); }
    @PostMapping("/login") public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest r) { return ResponseEntity.ok(auth.login(r)); }
    @GetMapping("/verify") public ResponseEntity<MessageResponse> verify(@RequestParam String token) { return ResponseEntity.ok(auth.verify(token)); }
    @PostMapping("/verify-email") public ResponseEntity<MessageResponse> verifyPost(@RequestParam String token) { return ResponseEntity.ok(auth.verify(token)); }
    @PostMapping("/logout") public ResponseEntity<MessageResponse> logout(@RequestHeader(value="Authorization", required=false) String header) {
        if (header == null || !header.startsWith("Bearer ")) return ResponseEntity.badRequest().body(new MessageResponse("Bearer token is required."));
        return ResponseEntity.ok(auth.logout(header.substring(7)));
    }
}
''')
add('controller/EmployeeController.java', r'''
package com.example.employee_backend.controller;

import com.example.employee_backend.dto.*;
import com.example.employee_backend.service.EmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService service;
    public EmployeeController(EmployeeService service) { this.service=service; }
    @GetMapping @PreAuthorize("hasRole('ADMIN')") public List<EmployeeResponse> all() { return service.all(); }
    @GetMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public EmployeeResponse byId(@PathVariable Long id) { return service.byId(id); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public EmployeeResponse update(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequest r) { return service.update(id,r); }
}
''')
add('controller/AttendanceController.java', r'''
package com.example.employee_backend.controller;

import com.example.employee_backend.dto.AttendanceResponse;
import com.example.employee_backend.service.AttendanceService;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service) { this.service=service; }
    @PostMapping("/check-in") public AttendanceResponse checkIn(Authentication a) { return service.checkIn(a.getName()); }
    @PostMapping("/checkin") public AttendanceResponse checkInAlias(Authentication a) { return service.checkIn(a.getName()); }
    @PostMapping("/check-out") public AttendanceResponse checkOut(Authentication a) { return service.checkOut(a.getName()); }
    @PostMapping("/checkout") public AttendanceResponse checkOutAlias(Authentication a) { return service.checkOut(a.getName()); }
    @GetMapping("/my") public List<AttendanceResponse> mine(Authentication a) { return service.mine(a.getName()); }
    @GetMapping("/all") @PreAuthorize("hasRole('ADMIN')") public List<AttendanceResponse> all() { return service.all(); }
}
''')
add('controller/LeaveController.java', r'''
package com.example.employee_backend.controller;

import com.example.employee_backend.dto.*;
import com.example.employee_backend.service.LeaveService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/leave")
public class LeaveController {
    private final LeaveService service;
    public LeaveController(LeaveService service) { this.service=service; }
    @PostMapping public LeaveResponse create(Authentication a, @Valid @RequestBody LeaveCreateRequest r) { return service.create(a.getName(),r); }
    @GetMapping("/my") public List<LeaveResponse> mine(Authentication a) { return service.mine(a.getName()); }
    @GetMapping("/all") @PreAuthorize("hasRole('ADMIN')") public List<LeaveResponse> all() { return service.all(); }
    @PutMapping("/{id}/approve") @PreAuthorize("hasRole('ADMIN')") public LeaveResponse approve(@PathVariable Long id) { return service.decide(id,true); }
    @PutMapping("/{id}/reject") @PreAuthorize("hasRole('ADMIN')") public LeaveResponse reject(@PathVariable Long id) { return service.decide(id,false); }
}
''')
add('controller/PayrollController.java', r'''
package com.example.employee_backend.controller;

import com.example.employee_backend.dto.*;
import com.example.employee_backend.service.PayrollService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/payroll")
public class PayrollController {
    private final PayrollService service;
    public PayrollController(PayrollService service) { this.service=service; }
    @GetMapping("/my") public List<PayrollResponse> mine(Authentication a) { return service.mine(a.getName()); }
    @GetMapping("/all") @PreAuthorize("hasRole('ADMIN')") public List<PayrollResponse> all() { return service.all(); }
    @PostMapping @PreAuthorize("hasRole('ADMIN')") public PayrollResponse create(@Valid @RequestBody PayrollUpsertRequest r) { return service.create(r); }
    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public PayrollResponse update(@PathVariable Long id, @Valid @RequestBody PayrollUpdateRequest r) { return service.update(id,r); }
}
''')

for rel, content in files.items():
    path = root / rel
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content)
print(f'Generated {len(files)} source files.')
