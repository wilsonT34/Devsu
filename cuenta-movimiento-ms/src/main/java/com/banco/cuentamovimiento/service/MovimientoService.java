package com.banco.cuentamovimiento.service;

import com.banco.cuentamovimiento.dto.MovimientoDTO;
import com.banco.cuentamovimiento.dto.ReporteDTO;
import com.banco.cuentamovimiento.entity.Cuenta;
import com.banco.cuentamovimiento.entity.Movimiento;
import com.banco.cuentamovimiento.exception.CuentaNotFoundException;
import com.banco.cuentamovimiento.exception.SaldoNoDisponibleException;
import com.banco.cuentamovimiento.repository.CuentaRepository;
import com.banco.cuentamovimiento.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final CuentaService cuentaService;
    private final ModelMapper modelMapper;

    @Transactional
    public MovimientoDTO registrarMovimiento(MovimientoDTO movimientoDTO) {
        // Buscar la cuenta
        Cuenta cuenta = cuentaRepository.findById(movimientoDTO.getNumeroCuenta())
            .orElseThrow(() -> new CuentaNotFoundException(
                "Cuenta no encontrada: " + movimientoDTO.getNumeroCuenta()));

        // Validar estado de la cuenta
        if (!cuenta.getEstado()) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        // Calcular nuevo saldo
        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(movimientoDTO.getValor());

        // Validar saldo disponible (F3)
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new SaldoNoDisponibleException("Saldo no disponible");
        }

        // Actualizar saldo de la cuenta
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        // Registrar movimiento
        Movimiento movimiento = new Movimiento();
        movimiento.setValor(movimientoDTO.getValor());
        movimiento.setSaldo(nuevoSaldo);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setCuenta(cuenta);

        // Determinar tipo de movimiento
        if (movimientoDTO.getValor().compareTo(BigDecimal.ZERO) > 0) {
            movimiento.setTipoMovimiento("Deposito");
        } else {
            movimiento.setTipoMovimiento("Retiro");
        }

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);
        
        MovimientoDTO response = modelMapper.map(movimientoGuardado, MovimientoDTO.class);
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        return response;
    }

    @Transactional(readOnly = true)
    public List<MovimientoDTO> listarMovimientosPorCuenta(String numeroCuenta) {
        return movimientoRepository.findByCuentaNumeroCuentaOrderByFechaDesc(numeroCuenta)
            .stream()
            .map(movimiento -> {
                MovimientoDTO dto = modelMapper.map(movimiento, MovimientoDTO.class);
                dto.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteDTO> generarReporteEstadoCuenta(
            String clienteId, 
            LocalDate fechaInicio, 
            LocalDate fechaFin) {
        
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository
            .findMovimientosByClienteAndFechas(clienteId, inicio, fin);

        return movimientos.stream()
            .map(movimiento -> {
                ReporteDTO reporte = new ReporteDTO();
                reporte.setFecha(movimiento.getFecha());
                reporte.setCliente(movimiento.getCuenta().getClienteid());
                reporte.setNumeroCuenta(movimiento.getCuenta().getNumeroCuenta());
                reporte.setTipo(movimiento.getCuenta().getTipoCuenta());
                reporte.setSaldoInicial(movimiento.getCuenta().getSaldoInicial());
                reporte.setEstado(movimiento.getCuenta().getEstado());
                reporte.setMovimiento(movimiento.getValor());
                reporte.setSaldoDisponible(movimiento.getSaldo());
                return reporte;
            })
            .collect(Collectors.toList());
    }
}