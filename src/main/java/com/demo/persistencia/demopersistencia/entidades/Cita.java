package com.demo.persistencia.demopersistencia.entidades;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

@ManyToOne
@JoinColumn(name = "paciente_id")
private Paciente paciente;

    private LocalDate fecha;
    private String hora;

    private String estado; // PENDIENTE, CANCELADA, COMPLETADA

    private String observacion;
    @Column(name = "medico_id")
private Long medicoId;
}
