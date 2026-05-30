package com.demo.persistencia.demopersistencia.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.persistencia.demopersistencia.entidades.Cita;
import com.demo.persistencia.demopersistencia.repositorio.CitaRepository;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    // ✅ GUARDAR CITA (JWT)
    public Cita guardar(Cita cita) {
        return citaRepository.save(cita);
    }

    // ✅ LISTAR TODAS LAS CITAS
    public List<Cita> listarCitas() {
        return citaRepository.findAll();
    }
public List<Cita> listarPorPaciente(Long pacienteId) {
    List<Cita> citas = citaRepository.findByPaciente_IdPaciente(pacienteId);

    if (citas == null) {
        return List.of();
    }

    return citas;
}

//medicos servicios
public List<Cita> citasPendientes(Long medicoId) {
    return citaRepository.findByMedicoIdAndEstado(medicoId, "PENDIENTE");
}

public boolean medicoOcupado(Long medicoId, LocalDate fecha, String hora) {
    return citaRepository.existsByMedicoIdAndFechaAndHoraAndEstado(
        medicoId, fecha, hora, "AGENDADA"
    );
} 

    // ✅ CANCELAR CITA
    public Cita cancelarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado("CANCELADA");

        return citaRepository.save(cita);
    }
public List<Cita> citasAgendadas(Long medicoId) {
    return citaRepository.findByMedicoIdAndEstado(medicoId, "AGENDADA");
}
    // ✅ REPROGRAMAR CITA
    public Cita reprogramarCita(Long id, LocalDate nuevaFecha, String nuevaHora) {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setFecha(nuevaFecha);
        cita.setHora(nuevaHora);

        return citaRepository.save(cita);
    }
}
