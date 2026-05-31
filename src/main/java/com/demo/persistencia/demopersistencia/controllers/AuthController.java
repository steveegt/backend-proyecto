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

            // ✅ VALIDAR REQUEST
            if (request == null || request.getUsername() == null || request.getPassword() == null) {
                throw new RuntimeException("Datos de login incompletos");
            }

            String username = request.getUsername().trim();
            String password = request.getPassword().trim();

            // ✅ BUSCAR USUARIO
            Usuario usuario = usuarioRepository.findByUsername(username);

            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado");
            }

            // ✅ VALIDAR PASSWORD
            String passwordBD = usuario.getPassword();

            if (passwordBD == null || passwordBD.isEmpty()) {
                throw new RuntimeException("El usuario no tiene contraseña válida");
            }

            boolean valido = false;

            try {
                // ✅ bcrypt (lo normal)
                valido = passwordEncoder.matches(password, passwordBD);
            } catch (Exception e) {
                // ✅ fallback por si el password estaba en texto plano
                valido = passwordBD.equals(password);
            }

            if (!valido) {
                throw new RuntimeException("Contraseña incorrecta");
            }

            // ✅ GENERAR TOKEN
            String token = JwtUtil.generarToken(usuario);

            LoginResponse response = new LoginResponse();
            response.setToken(token);
            response.setTipoUsuario(usuario.getTipoUsuario());

            return response;

        } catch (Exception e) {
            // ✅ ESTO ES CLAVE PARA VER EL ERROR REAL EN RAILWAY
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}