package com.banco.cuentamovimiento.repository;

import com.banco.cuentamovimiento.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author user
 */

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {
    
    List<Movimiento> findByCuentaNumeroCuentaOrderByFechaDesc(String numeroCuenta);
    
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.clienteid = :clienteId " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha DESC")
    List<Movimiento> findMovimientosByClienteAndFechas(
            @Param("clienteId") String clienteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
    
    // NUEVO MÉTODO: Buscar movimientos por cuenta y rango de fechas
    @Query("SELECT m FROM Movimiento m WHERE m.cuenta.numeroCuenta = :numeroCuenta " +
           "AND m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha ASC")
    List<Movimiento> findByCuentaAndFechaBetweenOrderByFechaAsc(
            @Param("numeroCuenta") String numeroCuenta,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}