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

        try {

            if (request.getUsername() == null || request.getPassword() == null) {
                throw new RuntimeException("Datos incompletos");
            }

            String username = request.getUsername().trim();
            String password = request.getPassword().trim();

            Usuario usuario = usuarioRepository.findByUsername(username);

            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            String passwordBD = usuario.getPassword();

            if (passwordBD == null || passwordBD.isEmpty()) {
                throw new RuntimeException("Usuario sin contraseña válida");
            }

            boolean valido = false;

            try {
                // ✅ Intentar con bcrypt
                valido = passwordEncoder.matches(password, passwordBD);
            } catch (Exception e) {
                // ✅ fallback por si el password viejo estaba en texto plano
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

        } catch (Exception e) {
            // ✅ IMPORTANTE: muestra el error real en logs de Railway
            e.printStackTrace();
            throw e; // 🔥 NO ocultar el error
        }
    }
}
