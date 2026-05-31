package com.demo.persistencia.demopersistencia.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(auth -> auth

                // ✅ LOGIN libre
                .requestMatchers("/auth/**").permitAll()

                // ✅ MÉDICO
                .requestMatchers("/api/medico/**").hasAuthority("MEDICO")
                .requestMatchers("/api/citas/**").hasAuthority("MEDICO")

                // ✅ PACIENTE
                .requestMatchers("/api/paciente/**").hasAuthority("PACIENTE")

                // ✅ ADMIN
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")

                // ✅ CUALQUIER OTRO
                .anyRequest().authenticated()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}