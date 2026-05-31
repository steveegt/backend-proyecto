package com.demo.persistencia.demopersistencia.controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.demo.persistencia.demopersistencia.entidades.Medico;
import com.demo.persistencia.demopersistencia.entidades.Usuario;
import com.demo.persistencia.demopersistencia.repositorio.MedicoRepository;
import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;
import com.demo.persistencia.demopersistencia.security.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/medico")
@CrossOrigin(origins = "*")
public class MedicoController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===============================
    // ✅ PERFIL (SOLO MEDICO)
    // ===============================
    @GetMapping("/mi-perfil")
    public Medico miPerfil(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = header.substring(7);
        String username = JwtUtil.getUsername(token);
        String rol = JwtUtil.getRol(token);

        if (!"MEDICO".equals(rol)) {
            throw new RuntimeException("Acceso denegado");
        }

        Usuario usuario = usuarioRepository.findByUsername(username);

        if (usuario == null || usuario.getMedicoId() == null) {
            throw new RuntimeException("Usuario sin médico");
        }

        return medicoRepository.findById(usuario.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
    }

    // ===============================
    // ✅ CREAR MÉDICO (SOLO ADMIN)
    // ===============================
    @PostMapping("/crear-con-usuario")
    public Map<String, String> crear(@RequestBody Map<String, Object> datos,
                                     HttpServletRequest request) {

        try {

            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new RuntimeException("Token inválido");
            }

            String token = header.substring(7);
            String rol = JwtUtil.getRol(token);

            if (!"ADMIN".equals(rol)) {
                throw new RuntimeException("Solo ADMIN puede crear médicos");
            }

            String username = (String) datos.get("username");

            if (usuarioRepository.findByUsername(username) != null) {
                throw new RuntimeException("El username ya existe");
            }

            Medico medico = new Medico();
            medico.setNombreCompleto((String) datos.get("nombreCompleto"));
            medico.setEspecialidad((String) datos.get("especialidad"));
            medico.setDireccion((String) datos.get("direccion"));
            medico.setObservacion((String) datos.get("observacion"));
            medico.setColegiado((String) datos.get("colegiado"));

            if (datos.get("edad") != null && !datos.get("edad").toString().isEmpty()) {
                medico.setEdad(Integer.parseInt(datos.get("edad").toString()));
            }

            medico.setFechaRegistro(LocalDate.now());

            medico = medicoRepository.save(medico);

            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode((String) datos.get("password")));
            usuario.setTipoUsuario("MEDICO");
            usuario.setMedicoId(medico.getMedicoId());

            usuarioRepository.save(usuario);

            return Map.of("mensaje", "Médico creado correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando médico: " + e.getMessage());
        }
    }

    // ===============================
    // ✅ BUSCAR MÉDICOS (SOLO ADMIN)
    // ===============================
    @GetMapping("/buscar")
    public List<Medico> buscar(@RequestParam String nombre,
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

        return medicoRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }

    // ===============================
    // ✅ ACTUALIZAR MÉDICO (SOLO ADMIN) 🔥 ESTE ERA EL PROBLEMA
    // ===============================
    @PutMapping("/actualizar/{id}")
    public Medico actualizar(@PathVariable Long id,
                              @RequestBody Medico m,
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

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        medico.setNombreCompleto(m.getNombreCompleto());
        medico.setEspecialidad(m.getEspecialidad());
        medico.setDireccion(m.getDireccion());
        medico.setEdad(m.getEdad());
        medico.setObservacion(m.getObservacion());
        medico.setColegiado(m.getColegiado());

        return medicoRepository.save(medico);
    }

    // ===============================
    // ✅ ELIMINAR MÉDICO (SOLO ADMIN)
    // ===============================
    @DeleteMapping("/eliminar/{id}")
    public void eliminar(@PathVariable Long id,
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

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        Usuario usuario = usuarioRepository.findByMedicoId(medico.getMedicoId());

        if (usuario != null) {
            usuarioRepository.deleteById(usuario.getId());
        }

        medicoRepository.deleteById(id);
    }
}