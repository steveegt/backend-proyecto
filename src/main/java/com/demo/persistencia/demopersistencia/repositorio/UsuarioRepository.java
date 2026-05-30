package com.demo.persistencia.demopersistencia.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.persistencia.demopersistencia.entidades.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsername(String username);
    Usuario findByPacienteId(Long pacienteId);
    Usuario findByMedicoId(Long medicoId);

}

