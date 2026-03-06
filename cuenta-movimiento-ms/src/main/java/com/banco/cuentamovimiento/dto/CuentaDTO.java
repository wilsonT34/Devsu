/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banco.cuentamovimiento.dto;




import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 *
 * @author user
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaDTO {
    
    @NotBlank(message = "El número de cuenta es obligatorio")
    private String numeroCuenta;
    
    @NotBlank(message = "El tipo de cuenta es obligatorio")
    private String tipoCuenta;
    
    @NotNull(message = "El saldo inicial es obligatorio")
    @PositiveOrZero(message = "El saldo inicial debe ser mayor o igual a cero")
    private BigDecimal saldoInicial;
    
    private BigDecimal saldoDisponible;
    
    @NotNull(message = "El estado es obligatorio")
    private Boolean estado;
    
    @NotBlank(message = "El ID del cliente es obligatorio")
    private String clienteid;
}