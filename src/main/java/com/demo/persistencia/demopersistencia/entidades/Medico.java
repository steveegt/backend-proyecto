package com.demo.persistencia.demopersistencia.entidades;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long medicoId;

    private String colegiado;

    @Column(name = "nombre_completo")
    private String nombreCompleto;

    private String especialidad;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    private String direccion;

    private Integer edad;

    private String observacion;
}