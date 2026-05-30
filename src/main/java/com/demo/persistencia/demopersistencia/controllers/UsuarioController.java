package com.demo.persistencia.demopersistencia.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.demo.persistencia.demopersistencia.entidades.Usuario;
import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    

    // ✅ BUSCAR USUARIO POR PACIENTE
    @GetMapping("/por-paciente/{id}")
    public Usuario obtenerPorPaciente(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findByPacienteId(id);

        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        return usuario;
    }

    // ✅ ACTUALIZAR USUARIO
    @PutMapping("/actualizar/{id}")
    public Usuario actualizar(@PathVariable Long id, @RequestBody Usuario u) {

        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setUsername(u.getUsername());
        usuario.setPassword(u.getPassword());

        return usuarioRepository.save(usuario);
    }
    //medicos
// ✅ BUSCAR USUARIO POR MÉDICO
@GetMapping("/por-medico/{id}")
public Usuario obtenerPorMedico(@PathVariable Long id) {

    Usuario usuario = usuarioRepository.findByMedicoId(id);

    if (usuario == null) {
        throw new RuntimeException("Usuario no encontrado");
    }

    return usuario;
}
}
