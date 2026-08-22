package com.example.leavemanagement.security;

import com.example.leavemanagement.model.User;
import com.example.leavemanagement.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public DatabaseUserDetailsService(UserRepository users){this.users=users;}
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User u=users.findByEmailIgnoreCase(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.withUsername(u.getEmail()).password(u.getPasswordHash()).disabled(!u.isActive()).authorities(new SimpleGrantedAuthority("ROLE_"+u.getRole().name())).build();
    }
}
