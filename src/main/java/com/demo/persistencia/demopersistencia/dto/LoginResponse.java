

package com.demo.persistencia.demopersistencia.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String tipoUsuario;
}
