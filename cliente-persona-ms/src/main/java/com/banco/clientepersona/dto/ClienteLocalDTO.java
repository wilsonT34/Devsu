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
public class ClienteLocalDTO {
    private String clienteId;
    private String nombre;
    private String identificacion;
    private Boolean activo;
}
