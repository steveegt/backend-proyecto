package com.demo.persistencia.demopersistencia.entidades;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {


    @Id
    @Column(name = "paciente_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPaciente;

    @Column(name = "nombre_completo")
    private String nombreCompleto;
      @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String direccion;
    private String telefono;


    @Column(name = "seguro_medico")
    private String seguro;


}
