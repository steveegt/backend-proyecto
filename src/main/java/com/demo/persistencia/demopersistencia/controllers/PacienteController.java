package com.demo.persistencia.demopersistencia.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.demo.persistencia.demopersistencia.entidades.Paciente;
import com.demo.persistencia.demopersistencia.entidades.Usuario;
import com.demo.persistencia.demopersistencia.repositorio.PacienteRepository;
import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;
import com.demo.persistencia.demopersistencia.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.password.PasswordEncoder;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===============================
    // ✅ CREAR PACIENTE + USUARIO
    // ===============================
    @PostMapping("/crear-con-usuario")
    public Map<String, String> crear(@RequestBody Map<String, Object> datos,
                                     HttpServletRequest request) {

        try {

            // ✅ VALIDAR TOKEN
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new RuntimeException("Token inválido");
            }

            String token = header.substring(7);
            String rol = JwtUtil.getRol(token);

            if (!"ADMIN".equals(rol)) {
                throw new RuntimeException("Solo ADMIN puede crear pacientes");
            }

            // ✅ USERNAME
            String username = (String) datos.get("username");

            if (usuarioRepository.findByUsername(username) != null) {
                throw new RuntimeException("El username ya existe");
            }

            // ✅ CREAR PACIENTE
            Paciente paciente = new Paciente();
            paciente.setNombreCompleto((String) datos.get("nombreCompleto"));
            paciente.setDireccion((String) datos.get("direccion"));
            paciente.setTelefono((String) datos.get("telefono"));
            paciente.setSeguro((String) datos.get("seguro"));

            // ✅ 🔥 FIX REAL DE FECHA
            if (datos.get("fechaNacimiento") != null) {

                String fechaStr = datos.get("fechaNacimiento").toString();

                // cortar si viene con formato completo ISO
                if (fechaStr.contains("T")) {
                    fechaStr = fechaStr.split("T")[0];
                }

                paciente.setFechaNacimiento(LocalDate.parse(fechaStr));
            }

            // ✅ GUARDAR PACIENTE
            paciente = pacienteRepository.save(paciente);

            // ✅ CREAR USUARIO
            Usuario usuario = new Usuario();
            usuario.setUsername(username);

            // ✅ ENCRIPTAR PASSWORD
            usuario.setPassword(
                passwordEncoder.encode((String) datos.get("password"))
            );

            usuario.setTipoUsuario("PACIENTE");
            usuario.setPacienteId(paciente.getIdPaciente());

            usuarioRepository.save(usuario);

            return Map.of("mensaje", "Paciente creado correctamente");

        } catch (Exception e) {

            // 🔥 IMPORTANTE: VER ERROR REAL EN LOGS
            e.printStackTrace();

            throw new RuntimeException("Error creando paciente: " + e.getMessage());
        }
    }

    // ===============================
    // ✅ PERFIL PACIENTE
    // ===============================
    @GetMapping("/mi-perfil")
    public Paciente miPerfil(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = header.substring(7);
        String username = JwtUtil.getUsername(token);

        Usuario usuario = usuarioRepository.findByUsername(username);

        return pacienteRepository.findById(usuario.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
    }

    // ===============================
    // ✅ BUSCAR
    // ===============================
    @GetMapping("/buscar")
    public List<Paciente> buscar(@RequestParam String nombre,
                                 HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = header.substring(7);
        String rol = JwtUtil.getRol(token);

        if (!"ADMIN".equals(rol)) {
            throw new RuntimeException("Acceso denegado");
        }

        return pacienteRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }

    // ===============================
    // ✅ ACTUALIZAR
    // ===============================
    @PutMapping("/actualizar/{id}")
    public Paciente actualizar(@PathVariable Long id,
                              @RequestBody Paciente p,
                              HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = header.substring(7);
        String rol = JwtUtil.getRol(token);

        if (!"ADMIN".equals(rol)) {
            throw new RuntimeException("Acceso denegado");
        }

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        paciente.setNombreCompleto(p.getNombreCompleto());
        paciente.setTelefono(p.getTelefono());
        paciente.setDireccion(p.getDireccion());
        paciente.setSeguro(p.getSeguro());

        return pacienteRepository.save(paciente);
    }
}