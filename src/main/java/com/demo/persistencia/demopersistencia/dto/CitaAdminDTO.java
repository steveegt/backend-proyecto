package com.demo.persistencia.demopersistencia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CitaAdminDTO {

    private String nombrePaciente;
    private String nombreMedico;
    private LocalDate fecha;
    private String hora;
    private String estado;
}