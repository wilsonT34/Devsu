package com.banco.clientepersona.service;

import com.banco.clientepersona.dto.ClienteDTO;
import com.banco.clientepersona.entity.Cliente;
import com.banco.clientepersona.entity.Persona;
import com.banco.clientepersona.event.ClienteEventPublisher;
import com.banco.clientepersona.exception.ClienteNotFoundException;
import com.banco.clientepersona.exception.DuplicateIdentificacionException;
import com.banco.clientepersona.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author user
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final ClienteEventPublisher eventPublisher;

    // ============ NUEVO: LISTAR CLIENTES ============
    @Transactional(readOnly = true)
    public List<ClienteDTO> listarClientes() {
        log.info("Listando todos los clientes");
        return clienteRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // ============ NUEVO: OBTENER CLIENTE POR ID ============
    @Transactional(readOnly = true)
    public ClienteDTO obtenerCliente(String clienteId) {
        log.info("Obteniendo cliente con ID: {}", clienteId);
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ClienteNotFoundException(
                "Cliente no encontrado con ID: " + clienteId));
        return convertToDTO(cliente);
    }

    // ============ CREAR CLIENTE (MODIFICADO) ============
    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        log.info("Creando nuevo cliente: {}", clienteDTO.getClienteId());
        
        // Validar si ya existe la identificación
        if (clienteRepository.existsByPersonaIdentificacion(clienteDTO.getIdentificacion())) {
            throw new DuplicateIdentificacionException(
                "Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
        }

        // Crear persona
        Persona persona = new Persona();
        persona.setNombre(clienteDTO.getNombre());
        persona.setGenero(clienteDTO.getGenero());
        persona.setEdad(clienteDTO.getEdad());
        persona.setIdentificacion(clienteDTO.getIdentificacion());
        persona.setDireccion(clienteDTO.getDireccion());
        persona.setTelefono(clienteDTO.getTelefono());

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteDTO.getClienteId());
        cliente.setContrasena(clienteDTO.getContrasena());
        cliente.setEstado(clienteDTO.getEstado());
        cliente.setPersona(persona);

        // Guardar
        Cliente clienteGuardado = clienteRepository.save(cliente);
        log.info("Cliente guardado con ID: {}", clienteGuardado.getClienteId());
        
        // Publicar evento
        try {
            eventPublisher.publishClienteCreado(
                clienteGuardado.getClienteId(),
                clienteGuardado.getPersona().getNombre(),
                clienteGuardado.getPersona().getIdentificacion()
            );
            log.info("Evento publicado para cliente: {}", clienteGuardado.getClienteId());
        } catch (Exception e) {
            log.error("Error al publicar evento: {}", e.getMessage());
            // No lanzamos la excepción para no afectar la creación del cliente
        }
        
        return convertToDTO(clienteGuardado);
    }

    // ============ ACTUALIZAR CLIENTE (MODIFICADO) ============
    @Transactional
    public ClienteDTO actualizarCliente(String clienteId, ClienteDTO clienteDTO) {
        log.info("Actualizando cliente con ID: {}", clienteId);
        
        Cliente clienteExistente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ClienteNotFoundException(
                "Cliente no encontrado con ID: " + clienteId));

        // Actualizar persona
        Persona persona = clienteExistente.getPersona();
        persona.setNombre(clienteDTO.getNombre());
        persona.setGenero(clienteDTO.getGenero());
        persona.setEdad(clienteDTO.getEdad());
        persona.setIdentificacion(clienteDTO.getIdentificacion());
        persona.setDireccion(clienteDTO.getDireccion());
        persona.setTelefono(clienteDTO.getTelefono());

        // Actualizar cliente
        clienteExistente.setContrasena(clienteDTO.getContrasena());
        clienteExistente.setEstado(clienteDTO.getEstado());

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        log.info("Cliente actualizado con ID: {}", clienteActualizado.getClienteId());
        
        // Publicar evento de actualización
        try {
            eventPublisher.publishClienteActualizado(
                clienteActualizado.getClienteId(),
                clienteActualizado.getPersona().getNombre(),
                clienteActualizado.getPersona().getIdentificacion()
            );
        } catch (Exception e) {
            log.error("Error al publicar evento de actualización: {}", e.getMessage());
        }
        
        return convertToDTO(clienteActualizado);
    }

    // ============ ELIMINAR CLIENTE ============
    @Transactional
    public void eliminarCliente(String clienteId) {
        log.info("Eliminando cliente con ID: {}", clienteId);
        
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException(
                "Cliente no encontrado con ID: " + clienteId);
        }
        
        // Publicar evento de eliminación
        try {
            eventPublisher.publishClienteEliminado(clienteId);
        } catch (Exception e) {
            log.error("Error al publicar evento de eliminación: {}", e.getMessage());
        }
        
        clienteRepository.deleteById(clienteId);
        log.info("Cliente eliminado con ID: {}", clienteId);
    }

    // ============ MÉTODO AUXILIAR PARA CONVERTIR ============
    private ClienteDTO convertToDTO(Cliente cliente) {
        ClienteDTO dto = modelMapper.map(cliente, ClienteDTO.class);
        
        // Asegurar que los datos de persona se mapean correctamente
        if (cliente.getPersona() != null) {
            dto.setNombre(cliente.getPersona().getNombre());
            dto.setGenero(cliente.getPersona().getGenero());
            dto.setEdad(cliente.getPersona().getEdad());
            dto.setIdentificacion(cliente.getPersona().getIdentificacion());
            dto.setDireccion(cliente.getPersona().getDireccion());
            dto.setTelefono(cliente.getPersona().getTelefono());
        }
        
        return dto;
    }
}