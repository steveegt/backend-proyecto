package com.demo.persistencia.demopersistencia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

@Override
protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header != null && header.startsWith("Bearer ")) {

        String token = header.substring(7);

        String username = JwtUtil.getUsername(token);
        String rol = JwtUtil.getRol(token);

        request.setAttribute("username", username);
        request.setAttribute("rol", rol); 
    }

    filterChain.doFilter(request, response);
}
}