package com.demo.persistencia.demopersistencia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {

    private String nombrePaciente;
    private LocalDate fecha;
    private String hora;
    private String estado;
    private String observacion;
}
