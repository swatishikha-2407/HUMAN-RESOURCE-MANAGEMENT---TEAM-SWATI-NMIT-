package com.example.leavemanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="employee_code", nullable=false, unique=true) private String employeeCode;
    @Column(name="first_name", nullable=false) private String firstName;
    @Column(name="last_name", nullable=false) private String lastName;
    @Column(nullable=false, unique=true) private String email;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Column(nullable=false) private String department;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role=Role.EMPLOYEE;
    @Column(nullable=false) private boolean active=true;
    @Column(name="created_at", nullable=false) private LocalDateTime createdAt=LocalDateTime.now();
    public User() {}
    public Long getId(){return id;} public String getEmployeeCode(){return employeeCode;} public void setEmployeeCode(String v){employeeCode=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;} public Role getRole(){return role;} public void setRole(Role v){role=v;} public boolean isActive(){return active;}
}
