package com.banco.cuentamovimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 *
 * @author user
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteEventDTO implements Serializable {
    private String clienteId;
    private String nombre;
    private String identificacion;
    private String evento; // "CREADO", "ACTUALIZADO", "ELIMINADO"
    private Long timestamp;
}