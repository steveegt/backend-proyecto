package com.demo.persistencia.demopersistencia.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.demo.persistencia.demopersistencia.entidades.Medico;

@Repository // ✅ IMPORTANTE
public interface MedicoRepository extends JpaRepository<Medico, Long> {

     List<Medico> findByNombreCompletoContainingIgnoreCase(String nombre);
     List<Medico> findAll();
        
}
