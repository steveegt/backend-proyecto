package com.demo.persistencia.demopersistencia.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Column(name = "tipo_usuario")
    private String tipoUsuario;

    @Column(name = "paciente_id")
    private Long pacienteId;

    @Column(name = "medico_id")
    private Long medicoId;
}
