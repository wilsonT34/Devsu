package com.banco.cuentamovimiento.controller;

import com.banco.cuentamovimiento.dto.MovimientoDTO;
import com.banco.cuentamovimiento.service.MovimientoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


/**
 *
 * @author user
 */

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    @GetMapping("/test")
    public String test() {
        return "Controlador de movimientos funcionando!";
    }

    @PostMapping
    public ResponseEntity<MovimientoDTO> registrarMovimiento(@Valid @RequestBody MovimientoDTO movimientoDTO) {
        MovimientoDTO nuevoMovimiento = movimientoService.registrarMovimiento(movimientoDTO);
        return new ResponseEntity<>(nuevoMovimiento, HttpStatus.CREATED);
    }

    @GetMapping("/cuenta/{numeroCuenta}")
    public ResponseEntity<List<MovimientoDTO>> listarMovimientosPorCuenta(@PathVariable String numeroCuenta) {
        List<MovimientoDTO> movimientos = movimientoService.listarMovimientosPorCuenta(numeroCuenta);
        return ResponseEntity.ok(movimientos);
    }
}