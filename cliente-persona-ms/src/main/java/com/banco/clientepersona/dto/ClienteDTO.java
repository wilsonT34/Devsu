package com.banco.clientepersona.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author user
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
    private String clienteId;
    private String contrasena;
    private Boolean estado;
    private String nombre;
    private String genero;
    private Integer edad;
    private String identificacion;
    private String direccion;
    private String telefono;
}