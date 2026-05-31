package com.demo.persistencia.demopersistencia.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.demo.persistencia.demopersistencia.dto.CitaAdminDTO;
import com.demo.persistencia.demopersistencia.entidades.Cita;
import com.demo.persistencia.demopersistencia.entidades.Paciente;
import com.demo.persistencia.demopersistencia.entidades.Medico;


public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    List<Cita> findByMedicoIdAndEstado(Long medicoId, String estado);

    boolean existsByMedicoIdAndFechaAndHoraAndEstado(
        Long medicoId, LocalDate fecha, String hora, String estado
    );

    // ✅ TODAS LAS CITAS
@Query("""
    SELECT new com.demo.persistencia.demopersistencia.dto.CitaAdminDTO(
        c.paciente.nombreCompleto,
        m.nombreCompleto,
        c.fecha,
        c.hora,
        c.estado
    )
    FROM Cita c, Medico m
    WHERE m.medicoId = c.medicoId
""")
List<CitaAdminDTO> obtenerTodas();


    // ✅ BUSCAR POR PACIENTE
@Query("""
    SELECT new com.demo.persistencia.demopersistencia.dto.CitaAdminDTO(
        c.paciente.nombreCompleto,
        m.nombreCompleto,
        c.fecha,
        c.hora,
        c.estado
    )
    FROM Cita c, Medico m
    WHERE m.medicoId = c.medicoId
      AND LOWER(c.paciente.nombreCompleto)
      LIKE LOWER(CONCAT('%', :nombre, '%'))
""")
List<CitaAdminDTO> buscarPorPaciente(@Param("nombre") String nombre);

// ✅ validar paciente
boolean existsByPaciente_IdPacienteAndFechaAndHoraAndEstado(
    Long pacienteId, LocalDate fecha, String hora, String estado
);
@Query("""
    SELECT c.hora FROM Cita c
    WHERE c.fecha = :fecha
    AND c.medicoId = :medicoId
    AND (c.estado = 'AGENDADA' OR c.estado = 'PENDIENTE')
""")
List<String> findHorasOcupadas(
    @Param("fecha") LocalDate fecha,
    @Param("medicoId") Long medicoId
);



}