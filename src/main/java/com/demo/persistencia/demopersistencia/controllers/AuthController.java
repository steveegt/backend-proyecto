package com.demo.persistencia.demopersistencia.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.demo.persistencia.demopersistencia.dto.LoginRequest;
import com.demo.persistencia.demopersistencia.dto.LoginResponse;
import com.demo.persistencia.demopersistencia.entidades.Usuario;
import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;
import com.demo.persistencia.demopersistencia.security.JwtUtil;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        String username = request.getUsername().trim();
        String password = request.getPassword().trim();

        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        String passwordBD = usuario.getPassword();

        boolean valido = false;

        try {
            // ✅ SOLO USAR bcrypt SIEMPRE
            valido = passwordEncoder.matches(password, passwordBD);

        } catch (Exception e) {
            // fallback si algo raro pasa
            valido = passwordBD.equals(password);
        }

        if (!valido) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = JwtUtil.generarToken(usuario);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTipoUsuario(usuario.getTipoUsuario());

        return response;
    }
}