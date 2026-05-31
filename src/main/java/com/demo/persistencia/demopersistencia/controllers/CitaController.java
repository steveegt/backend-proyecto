package com.demo.persistencia.demopersistencia.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.demo.persistencia.demopersistencia.dto.CitaAdminDTO;
import com.demo.persistencia.demopersistencia.dto.CitaDTO;
import com.demo.persistencia.demopersistencia.entidades.Cita;
import com.demo.persistencia.demopersistencia.entidades.Medico;
import com.demo.persistencia.demopersistencia.entidades.Paciente;
import com.demo.persistencia.demopersistencia.entidades.Usuario;
import com.demo.persistencia.demopersistencia.repositorio.CitaRepository;
import com.demo.persistencia.demopersistencia.repositorio.MedicoRepository;
import com.demo.persistencia.demopersistencia.repositorio.PacienteRepository;
import com.demo.persistencia.demopersistencia.repositorio.UsuarioRepository;
import com.demo.persistencia.demopersistencia.security.JwtUtil;
import com.demo.persistencia.demopersistencia.services.CitaService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CitaRepository citaRepository;

    // ✅ CREAR (PACIENTE)
    @PostMapping("/crear")
    public Cita crear(@RequestBody CitaDTO dto, HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = header.substring(7);
        String username = JwtUtil.getUsername(token);
        String rol = JwtUtil.getRol(token);

        if (!"PACIENTE".equals(rol)) {
            throw new RuntimeException("Solo pacientes pueden crear citas");
        }

        Usuario usuario = usuarioRepository.findByUsername(username);

        Paciente paciente = pacienteRepository.findById(usuario.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente no existe"));

        List<Medico> medicos = medicoRepository.findAll();

        Long medicoAsignado = null;

        for (Medico medico : medicos) {

            boolean ocupado =
                    citaRepository.existsByMedicoIdAndFechaAndHoraAndEstado(
                            medico.getMedicoId(), dto.getFecha(), dto.getHora(), "AGENDADA"
                    ) ||
                    citaRepository.existsByMedicoIdAndFechaAndHoraAndEstado(
                            medico.getMedicoId(), dto.getFecha(), dto.getHora(), "PENDIENTE"
                    );

            if (!ocupado) {
                medicoAsignado = medico.getMedicoId();
                break;
            }
        }

        if (medicoAsignado == null) {
            throw new RuntimeException("No hay médicos disponibles");
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setFecha(dto.getFecha());
        cita.setHora(dto.getHora());
        cita.setObservacion(dto.getObservacion());
        cita.setEstado("PENDIENTE");
        cita.setMedicoId(medicoAsignado);

        return citaRepository.save(cita);
    }

    // ✅ CITAS MÉDICO PENDIENTES
    @GetMapping("/citas-medico")
    public List<Cita> citasMedico(HttpServletRequest request) {

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

        return citaRepository.findByMedicoIdAndEstado(
                usuario.getMedicoId(), "PENDIENTE"
        );
    }

    // ✅ CITAS MÉDICO AGENDADAS
    @GetMapping("/citas-medico-agendadas")
    public List<Cita> citasMedicoAgendadas(HttpServletRequest request) {

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

        return citaRepository.findByMedicoIdAndEstado(
                usuario.getMedicoId(), "AGENDADA"
        );
    }

    // ✅ ACEPTAR CITA
    @PutMapping("/aceptar/{id}")
    public void aceptar(@PathVariable Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado("AGENDADA");
        citaRepository.save(cita);
    }

    // ✅ RECHAZAR CITA
    @PutMapping("/rechazar/{id}")
    public void rechazar(@PathVariable Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado("RECHAZADA");
        citaRepository.save(cita);
    }

    // ✅ ADMIN - TODAS
    @GetMapping("/todas")
    public List<CitaAdminDTO> obtenerTodas(HttpServletRequest request) {

        String rol = JwtUtil.getRol(request.getHeader("Authorization").substring(7));

        if (!"ADMIN".equals(rol)) {
            throw new RuntimeException("Acceso denegado");
        }

        return citaRepository.obtenerTodas();
    }

    // ✅ MIS CITAS (PACIENTE)
    @GetMapping("/mis-citas")
    public List<Cita> misCitas(HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        String username = JwtUtil.getUsername(token);

        Usuario usuario = usuarioRepository.findByUsername(username);

        return citaService.listarPorPaciente(usuario.getPacienteId());
    }
}