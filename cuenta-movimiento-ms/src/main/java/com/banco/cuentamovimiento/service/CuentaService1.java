package com.banco.cuentamovimiento.service;

import com.banco.cuentamovimiento.dto.CuentaDTO;
import com.banco.cuentamovimiento.entity.Cuenta;
import com.banco.cuentamovimiento.exception.CuentaNotFoundException;
import com.banco.cuentamovimiento.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */

@Service
@RequiredArgsConstructor
public class CuentaService1 {

    private final CuentaRepository cuentaRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CuentaDTO crearCuenta(CuentaDTO cuentaDTO) {
        // Validar si ya existe
        if (cuentaRepository.existsByNumeroCuenta(cuentaDTO.getNumeroCuenta())) {
            throw new RuntimeException("Ya existe una cuenta con el número: " + 
                cuentaDTO.getNumeroCuenta());
        }

        // Crear cuenta
        Cuenta cuenta = modelMapper.map(cuentaDTO, Cuenta.class);
        cuenta.setSaldoDisponible(cuentaDTO.getSaldoInicial());
        
        Cuenta cuentaGuardada = cuentaRepository.save(cuenta);
        return modelMapper.map(cuentaGuardada, CuentaDTO.class);
    }

    @Transactional(readOnly = true)
    public CuentaDTO obtenerCuenta(String numeroCuenta) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
            .orElseThrow(() -> new CuentaNotFoundException(
                "Cuenta no encontrada: " + numeroCuenta));
        return modelMapper.map(cuenta, CuentaDTO.class);
    }

    @Transactional(readOnly = true)
    public List<CuentaDTO> listarCuentasPorCliente(String clienteId) {
        return cuentaRepository.findByClienteid(clienteId).stream()
            .map(cuenta -> modelMapper.map(cuenta, CuentaDTO.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public CuentaDTO actualizarCuenta(String numeroCuenta, CuentaDTO cuentaDTO) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
            .orElseThrow(() -> new CuentaNotFoundException(
                "Cuenta no encontrada: " + numeroCuenta));

        cuenta.setTipoCuenta(cuentaDTO.getTipoCuenta());
        cuenta.setEstado(cuentaDTO.getEstado());

        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);
        return modelMapper.map(cuentaActualizada, CuentaDTO.class);
    }

    @Transactional
    public void actualizarSaldo(String numeroCuenta, BigDecimal nuevoSaldo) {
        Cuenta cuenta = cuentaRepository.findById(numeroCuenta)
            .orElseThrow(() -> new CuentaNotFoundException(
                "Cuenta no encontrada: " + numeroCuenta));
        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);
    }
}