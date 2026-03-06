package com.banco.cuentamovimiento.controller;

import com.banco.cuentamovimiento.dto.ReporteDTO;
import com.banco.cuentamovimiento.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author user
 */

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final ReporteService reporteService;

    /**
     * Endpoint para generar reporte de estado de cuenta
     * Formato: /reportes?fechaInicio=2026-03-01&fechaFin=2026-03-04&cliente=1
     */
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> generarReporte(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam String cliente) {
        
        log.info("Solicitud de reporte - Cliente: {}, Desde: {}, Hasta: {}", 
            cliente, fechaInicio, fechaFin);
        
        List<ReporteDTO> reporte = reporteService.generarReporteEstadoCuenta(
            cliente, fechaInicio, fechaFin);
        
        return ResponseEntity.ok(reporte);
    }
    
    /**
     * Endpoint alternativo con formato más amigable
     */
    @GetMapping("/detallado")
    public ResponseEntity<?> generarReporteDetallado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam String cliente) {
        
        var reporte = reporteService.generarReporteCompleto(cliente, fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }
}