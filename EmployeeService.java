package com.example.employeeattendance.service;

import com.example.employeeattendance.dto.EmployeeDtos;
import com.example.employeeattendance.entity.EmployeeProfile;
import com.example.employeeattendance.entity.Enums;
import com.example.employeeattendance.entity.User;
import com.example.employeeattendance.exception.ApiException;
import com.example.employeeattendance.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeeDtos.EmployeeResponse create(EmployeeDtos.CreateEmployeeRequest request) {
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
        user.setProfile(toProfile(user, request));
        return toResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDtos.EmployeeResponse> findAll() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDtos.EmployeeResponse findByEmployeeId(String employeeId) {
        return toResponse(getUser(employeeId));
    }

    @Transactional(readOnly = true)
    public EmployeeDtos.EmployeeResponse findCurrent(String email) {
        return toResponse(getUserByEmail(email));
    }

    @Transactional
    public EmployeeDtos.EmployeeResponse update(String employeeId, EmployeeDtos.UpdateEmployeeRequest request) {
        User user = getUser(employeeId);
        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email is already registered");
        }
        user.setEmail(request.email().toLowerCase());
        EmployeeProfile profile = user.getProfile();
        profile.setName(request.name());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setDepartment(request.department());
        profile.setDesignation(request.designation());
        profile.setJoiningDate(AuthService.parseDate(request.joiningDate()));
        profile.setProfilePicture(request.profilePicture());
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public void delete(String employeeId) {
        userRepository.delete(getUser(employeeId));
    }

    public User getUser(String employeeId) {
        return userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Employee not found"));
    }

    private EmployeeProfile toProfile(User user, EmployeeDtos.CreateEmployeeRequest request) {
        EmployeeProfile profile = new EmployeeProfile();
        profile.setUser(user);
        profile.setName(request.name());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setDepartment(request.department());
        profile.setDesignation(request.designation());
        profile.setJoiningDate(AuthService.parseDate(request.joiningDate()));
        profile.setProfilePicture(request.profilePicture());
        return profile;
    }

    private EmployeeDtos.EmployeeResponse toResponse(User user) {
        EmployeeProfile profile = user.getProfile();
        return new EmployeeDtos.EmployeeResponse(
                profile.getId(), user.getEmployeeId(), user.getEmail(), user.getRole().name(),
                profile.getName(), profile.getPhone(), profile.getAddress(), profile.getDepartment(),
                profile.getDesignation(), profile.getJoiningDate().toString(), profile.getProfilePicture());
    }
}
