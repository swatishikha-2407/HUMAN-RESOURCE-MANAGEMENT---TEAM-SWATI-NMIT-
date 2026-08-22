package com.example.employeeattendance.repository;

import com.example.employeeattendance.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUserEmployeeId(String employeeId);
}
