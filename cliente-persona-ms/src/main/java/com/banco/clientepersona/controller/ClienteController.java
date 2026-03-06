package com.banco.clientepersona.controller;

import com.banco.clientepersona.dto.ClienteDTO;
import com.banco.clientepersona.service.ClienteService;
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
@RequestMapping("/api/clientes")  // ¡Esta línea es CRÍTICA!
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // Endpoint de prueba simple
    @GetMapping("/test")
    public String test() {
        return "Controlador funcionando!";
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
        return new ResponseEntity<>(nuevoCliente, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        List<ClienteDTO> clientes = clienteService.listarClientes();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> obtenerCliente(@PathVariable String clienteId) {
        ClienteDTO cliente = clienteService.obtenerCliente(clienteId);
        return ResponseEntity.ok(cliente);
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteDTO> actualizarCliente(
            @PathVariable String clienteId,
            @Valid @RequestBody ClienteDTO clienteDTO) {
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(clienteId, clienteDTO);
        return ResponseEntity.ok(clienteActualizado);
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable String clienteId) {
        clienteService.eliminarCliente(clienteId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{clienteId}/estado")
    public ResponseEntity<ClienteDTO> cambiarEstado(
            @PathVariable String clienteId,
            @RequestParam Boolean estado) {
        ClienteDTO cliente = clienteService.obtenerCliente(clienteId);
        cliente.setEstado(estado);
        ClienteDTO clienteActualizado = clienteService.actualizarCliente(clienteId, cliente);
        return ResponseEntity.ok(clienteActualizado);
    }
}