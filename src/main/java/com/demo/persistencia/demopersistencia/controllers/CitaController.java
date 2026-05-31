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

    // ===============================
    // ✅ CREAR (PACIENTE)
    // ===============================
    @PostMapping("/crear")
    public Cita crear(@RequestBody CitaDTO dto, HttpServletRequest request) {

        try {

            // ✅ VALIDAR TOKEN
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

            if (usuario == null || usuario.getPacienteId() == null) {
                throw new RuntimeException("Usuario inválido");
            }

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

            boolean pacienteOcupado =
                    citaRepository.existsByPaciente_IdPacienteAndFechaAndHoraAndEstado(
                            paciente.getIdPaciente(), dto.getFecha(), dto.getHora(), "AGENDADA"
                    ) ||
                    citaRepository.existsByPaciente_IdPacienteAndFechaAndHoraAndEstado(
                            paciente.getIdPaciente(), dto.getFecha(), dto.getHora(), "PENDIENTE"
                    );

            if (pacienteOcupado) {
                throw new RuntimeException("Ya tienes una cita en esa hora");
            }

            Cita cita = new Cita();
            cita.setPaciente(paciente);

            // ✅ SEGURIDAD EXTRA FECHA
            LocalDate fecha = dto.getFecha();

            cita.setFecha(fecha);
            cita.setHora(dto.getHora());
            cita.setObservacion(dto.getObservacion());
            cita.setEstado("PENDIENTE");
            cita.setMedicoId(medicoAsignado);

            return citaRepository.save(cita);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creando cita: " + e.getMessage());
        }
    }

    // ===============================
    // ✅ ADMIN - TODAS
    // ===============================
    @GetMapping("/todas")
    public List<CitaAdminDTO> obtenerTodas(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String rol = JwtUtil.getRol(header.substring(7));

        if (!"ADMIN".equals(rol)) {
            throw new RuntimeException("Acceso denegado");
        }

        return citaRepository.obtenerTodas();
    }

    // ===============================
    // ✅ ADMIN - BUSCAR
    // ===============================
    @GetMapping("/buscar")
    public List<CitaAdminDTO> buscar(@RequestParam String nombre, HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String rol = JwtUtil.getRol(header.substring(7));

        if (!"ADMIN".equals(rol)) {
            throw new RuntimeException("Acceso denegado");
        }

        return citaRepository.buscarPorPaciente(nombre);
    }

    // ===============================
    // ✅ HORARIOS
    // ===============================
    @GetMapping("/horarios-no-disponibles")
    public List<String> horariosNoDisponibles(@RequestParam String fecha) {

        try {

            // ✅ FIX DE FECHA (igual que pacientes)
            if (fecha.contains("T")) {
                fecha = fecha.split("T")[0];
            }

            LocalDate fechaParsed = LocalDate.parse(fecha);

            List<Medico> medicos = medicoRepository.findAll();

            List<String> horas = List.of(
                    "08:00 - 09:00", "09:00 - 10:00",
                    "10:00 - 11:00", "11:00 - 12:00",
                    "12:00 - 13:00", "14:00 - 15:00",
                    "15:00 - 16:00", "16:00 - 17:00"
            );

            List<String> bloqueadas = new java.util.ArrayList<>();

            for (String hora : horas) {

                int ocupados = 0;

                for (Medico medico : medicos) {

                    boolean ocupado =
                            citaRepository.existsByMedicoIdAndFechaAndHoraAndEstado(
                                    medico.getMedicoId(),
                                    fechaParsed,
                                    hora,
                                    "AGENDADA"
                            ) ||
                            citaRepository.existsByMedicoIdAndFechaAndHoraAndEstado(
                                    medico.getMedicoId(),
                                    fechaParsed,
                                    hora,
                                    "PENDIENTE"
                            );

                    if (ocupado) ocupados++;
                }

                if (ocupados == medicos.size()) {
                    bloqueadas.add(hora);
                }
            }

            return bloqueadas;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error en horarios: " + e.getMessage());
        }
    }

    // ===============================
    // ✅ MIS CITAS
    // ===============================
    @GetMapping("/mis-citas")
    public List<Cita> misCitas(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = header.substring(7);
        String username = JwtUtil.getUsername(token);

        Usuario usuario = usuarioRepository.findByUsername(username);

        return citaService.listarPorPaciente(usuario.getPacienteId());
    }
}