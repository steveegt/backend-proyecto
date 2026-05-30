package com.demo.persistencia.demopersistencia.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.demo.persistencia.demopersistencia.entidades.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long>{
List<Paciente> findByNombreCompletoContainingIgnoreCase(String nombre);
}
