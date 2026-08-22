package com.example.employeeattendance.config;

import com.example.employeeattendance.entity.EmployeeProfile;
import com.example.employeeattendance.entity.Enums;
import com.example.employeeattendance.entity.User;
import com.example.employeeattendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class AdminSeeder {
    @Bean
    CommandLineRunner seedAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${APP_ADMIN_EMPLOYEE_ID:}") String employeeId,
            @Value("${APP_ADMIN_EMAIL:}") String email,
            @Value("${APP_ADMIN_PASSWORD:}") String password) {
        return args -> {
            if (employeeId.isBlank() || email.isBlank() || password.isBlank()
                    || userRepository.existsByEmailIgnoreCase(email)) {
                return;
            }
            User admin = new User();
            admin.setEmployeeId(employeeId);
            admin.setEmail(email.toLowerCase());
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setRole(Enums.Role.ADMIN);

            EmployeeProfile profile = new EmployeeProfile();
            profile.setUser(admin);
            profile.setName("System Administrator");
            profile.setPhone("N/A");
            profile.setAddress("N/A");
            profile.setDepartment("Administration");
            profile.setDesignation("Administrator");
            profile.setJoiningDate(LocalDate.now());
            admin.setProfile(profile);
            userRepository.save(admin);
        };
    }
}
