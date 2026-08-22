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
        return org.springframework.security.core.userdetails.User.withUsername(u.getEmail()).password(u.getPasswordHash())
            .roles(u.getRole().name()).disabled(!u.isActive()).build();
    }
}
