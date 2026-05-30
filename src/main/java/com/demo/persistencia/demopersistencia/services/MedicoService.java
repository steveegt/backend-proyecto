package com.demo.persistencia.demopersistencia.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.persistencia.demopersistencia.entidades.Medico;
import com.demo.persistencia.demopersistencia.repositorio.MedicoRepository;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    public List<Medico> buscar(String nombre) {
        return medicoRepository.findByNombreCompletoContainingIgnoreCase(nombre);
    }

    public Medico guardar(Medico m) {
        return medicoRepository.save(m);
    }

    public Medico actualizar(Long id, Medico m) {

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));

        medico.setNombreCompleto(m.getNombreCompleto());
        medico.setEspecialidad(m.getEspecialidad());
        medico.setDireccion(m.getDireccion());
        medico.setEdad(m.getEdad());
        medico.setObservacion(m.getObservacion());

        return medicoRepository.save(medico);
    }
}
