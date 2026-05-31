package com.demo.persistencia.demopersistencia.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import com.demo.persistencia.demopersistencia.entidades.Usuario;
import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class InitData {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {

        if (usuarioRepository.findByUsername("steve") == null) {

            Usuario admin = new Usuario();
            admin.setUsername("steve"); // 👉 usa minúsculas mejor
            admin.setPassword(passwordEncoder.encode("1234"));
            admin.setTipoUsuario("ADMIN");

            usuarioRepository.save(admin);

            System.out.println("✅ ADMIN CREADO: steve / 1234");
        }
    }
}