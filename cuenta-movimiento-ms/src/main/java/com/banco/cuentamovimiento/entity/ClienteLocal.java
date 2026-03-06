package com.banco.cuentamovimiento.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author user
 */

@Entity
@Table(name = "clientes_locales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteLocal {
    
    @Id
    private String clienteId;
    
    private String nombre;
    
    private String identificacion;
    
    private Boolean activo = true;
}