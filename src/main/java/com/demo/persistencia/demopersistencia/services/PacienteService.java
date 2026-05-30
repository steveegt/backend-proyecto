package com.demo.persistencia.demopersistencia.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.persistencia.demopersistencia.dto.PacienteDTO;
import com.demo.persistencia.demopersistencia.entidades.Paciente;
import com.demo.persistencia.demopersistencia.repositorio.PacienteRepository;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    // ✅ LISTAR TODOS LOS PACIENTES (DTO)
    public List<PacienteDTO> consultarPacientes() {

        List<Paciente> pacientes = pacienteRepository.findAll();

        return pacientes.stream()
                .map(p -> new PacienteDTO(
                        p.getNombreCompleto(),
                        p.getFechaNacimiento(),
                        p.getDireccion(),
                        p.getTelefono(),
                        p.getSeguro()
                ))
                .collect(Collectors.toList());
    }

    // ✅ REGISTRAR PACIENTE
    public Paciente registrarPaciente(PacienteDTO dto) {

        Paciente paciente = new Paciente();
        paciente.setNombreCompleto(dto.getNombreCompleto());
        paciente.setFechaNacimiento(dto.getFechaNacimiento());
        paciente.setDireccion(dto.getDireccion());
        paciente.setTelefono(dto.getTelefono());
        paciente.setSeguro(dto.getSeguro());

        return pacienteRepository.save(paciente);
    }

}
