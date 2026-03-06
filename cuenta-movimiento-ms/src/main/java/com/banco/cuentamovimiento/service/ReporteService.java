

package com.banco.cuentamovimiento.service;

import com.banco.cuentamovimiento.dto.ReporteDTO;
import com.banco.cuentamovimiento.entity.Cuenta;
import com.banco.cuentamovimiento.entity.Movimiento;
import com.banco.cuentamovimiento.repository.CuentaRepository;
import com.banco.cuentamovimiento.repository.MovimientoRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author user
 */


@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteService {

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;

    @Transactional(readOnly = true)
    public List<ReporteDTO> generarReporteEstadoCuenta(
            String clienteId, 
            LocalDate fechaInicio, 
            LocalDate fechaFin) {
        
        log.info("Generando reporte para cliente: {} desde: {} hasta: {}", 
            clienteId, fechaInicio, fechaFin);
        
        List<ReporteDTO> reporte = new ArrayList<>();
        
        // 1. Obtener todas las cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteid(clienteId);
        
        if (cuentas.isEmpty()) {
            log.warn("El cliente {} no tiene cuentas asociadas", clienteId);
            return reporte;
        }
        
        // 2. Para cada cuenta, obtener sus movimientos en el rango de fechas
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        for (Cuenta cuenta : cuentas) {
            // Agregar el saldo inicial de la cuenta como un registro (opcional)
            // Esto depende de cómo quieras mostrar el reporte
            
            // Obtener movimientos de la cuenta en el rango
            List<Movimiento> movimientos = movimientoRepository
                .findByCuentaAndFechaBetweenOrderByFechaAsc(
                    cuenta.getNumeroCuenta(), inicio, fin);
            
            // Convertir cada movimiento a ReporteDTO
            for (Movimiento mov : movimientos) {
                ReporteDTO dto = new ReporteDTO();
                dto.setFecha(mov.getFecha());
                dto.setCliente(clienteId);
                dto.setNumeroCuenta(cuenta.getNumeroCuenta());
                dto.setTipo(cuenta.getTipoCuenta());
                dto.setSaldoInicial(cuenta.getSaldoInicial());
                dto.setEstado(cuenta.getEstado());
                dto.setMovimiento(mov.getValor());
                dto.setSaldoDisponible(mov.getSaldo());
                
                reporte.add(dto);
            }
            
            // Si la cuenta no tiene movimientos en el rango, 
            // podrías agregar un registro con saldo actual
            if (movimientos.isEmpty()) {
                ReporteDTO dto = new ReporteDTO();
                dto.setFecha(LocalDateTime.now());
                dto.setCliente(clienteId);
                dto.setNumeroCuenta(cuenta.getNumeroCuenta());
                dto.setTipo(cuenta.getTipoCuenta());
                dto.setSaldoInicial(cuenta.getSaldoInicial());
                dto.setEstado(cuenta.getEstado());
                dto.setMovimiento(BigDecimal.ZERO);
                dto.setSaldoDisponible(cuenta.getSaldoDisponible());
                
                reporte.add(dto);
            }
        }
        
        log.info("Reporte generado con {} registros", reporte.size());
        return reporte;
    }
    
    /**
     * Versión alternativa que agrupa por cuenta y muestra saldos
     */
    @Transactional(readOnly = true)
    public List<Object> generarReporteCompleto(
            String clienteId, 
            LocalDate fechaInicio, 
            LocalDate fechaFin) {
        
        List<Object> reporteCompleto = new ArrayList<>();
        
        // Obtener cuentas del cliente
        List<Cuenta> cuentas = cuentaRepository.findByClienteid(clienteId);
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);
        
        for (Cuenta cuenta : cuentas) {
            // Crear objeto con información de la cuenta
            var cuentaInfo = new Object() {
                public final String numeroCuenta = cuenta.getNumeroCuenta();
                public final String tipo = cuenta.getTipoCuenta();
                public final BigDecimal saldoActual = cuenta.getSaldoDisponible();
                public final Boolean estado = cuenta.getEstado();
                public List<ReporteDTO> movimientos = new ArrayList<>();
            };
            
            // Obtener movimientos de la cuenta
            List<Movimiento> movimientos = movimientoRepository
                .findByCuentaAndFechaBetweenOrderByFechaAsc(
                    cuenta.getNumeroCuenta(), inicio, fin);
            
            for (Movimiento mov : movimientos) {
                ReporteDTO dto = new ReporteDTO();
                dto.setFecha(mov.getFecha());
                dto.setMovimiento(mov.getValor());
                dto.setSaldoDisponible(mov.getSaldo());
                cuentaInfo.movimientos.add(dto);
            }
            
            reporteCompleto.add(cuentaInfo);
        }
        
        return reporteCompleto;
    }
}