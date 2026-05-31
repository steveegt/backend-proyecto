package com.demo.persistencia.demopersistencia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            try {
                String token = header.substring(7);

                // ✅ intentar leer token
                String username = JwtUtil.getUsername(token);
                String rol = JwtUtil.getRol(token);

                // ✅ guardar en request (opcional)
                request.setAttribute("username", username);
                request.setAttribute("rol", rol);

            } catch (Exception e) {
                // 🔥 CLAVE: NO BLOQUEAR
                System.out.println("⚠️ Token inválido o expirado");
            }
        }

        // ✅ SIEMPRE continuar (esto evita 403)
        filterChain.doFilter(request, response);
    }
}