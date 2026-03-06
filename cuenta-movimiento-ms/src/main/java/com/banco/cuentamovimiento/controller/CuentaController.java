package com.banco.cuentamovimiento.controller;

import com.banco.cuentamovimiento.dto.CuentaDTO;
import com.banco.cuentamovimiento.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 * @author user
 */

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @GetMapping("/test")
    public String test() {
        return "Controlador de cuentas funcionando!";
    }

    @PostMapping
    public ResponseEntity<CuentaDTO> crearCuenta(@Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO nuevaCuenta = cuentaService.crearCuenta(cuentaDTO);
        return new ResponseEntity<>(nuevaCuenta, HttpStatus.CREATED);
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> obtenerCuenta(@PathVariable String numeroCuenta) {
        CuentaDTO cuenta = cuentaService.obtenerCuenta(numeroCuenta);
        return ResponseEntity.ok(cuenta);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaDTO>> listarCuentasPorCliente(@PathVariable String clienteId) {
        List<CuentaDTO> cuentas = cuentaService.listarCuentasPorCliente(clienteId);
        return ResponseEntity.ok(cuentas);
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaDTO> actualizarCuenta(
            @PathVariable String numeroCuenta,
            @Valid @RequestBody CuentaDTO cuentaDTO) {
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(numeroCuenta, cuentaDTO);
        return ResponseEntity.ok(cuentaActualizada);
    }

    @PatchMapping("/{numeroCuenta}/estado")
    public ResponseEntity<CuentaDTO> cambiarEstado(
            @PathVariable String numeroCuenta,
            @RequestParam Boolean estado) {
        CuentaDTO cuenta = cuentaService.obtenerCuenta(numeroCuenta);
        cuenta.setEstado(estado);
        CuentaDTO cuentaActualizada = cuentaService.actualizarCuenta(numeroCuenta, cuenta);
        return ResponseEntity.ok(cuentaActualizada);
    }
}