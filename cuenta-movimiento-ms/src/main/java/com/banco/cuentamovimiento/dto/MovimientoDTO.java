/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.cuentamovimiento.dto;





import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author user
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoDTO {
    
    private Long id;
    private LocalDateTime fecha;
    private String tipoMovimiento;
    
    @NotNull(message = "El valor del movimiento es obligatorio")
    private BigDecimal valor;
    
    private BigDecimal saldo;
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;
}