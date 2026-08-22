package com.example.leavemanagement.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt; private final DatabaseUserDetailsService users;
    public JwtAuthFilter(JwtService jwt,DatabaseUserDetailsService users){this.jwt=jwt;this.users=users;}
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,java.io.IOException {
        String h=req.getHeader("Authorization");
        if(h!=null && h.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication()==null){
            String token=h.substring(7);
            if(jwt.valid(token)){ String email=jwt.username(token); UserDetails details=users.loadUserByUsername(email); UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(details,null,details.getAuthorities()); auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req)); SecurityContextHolder.getContext().setAuthentication(auth); }
        }
        chain.doFilter(req,res);
    }
}
