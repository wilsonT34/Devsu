
package com.banco.clientepersona.service;

import com.banco.clientepersona.dto.ClienteDTO;
import com.banco.clientepersona.entity.Cliente;
import com.banco.clientepersona.entity.Persona;
import com.banco.clientepersona.exception.ClienteNotFoundException;
import com.banco.clientepersona.exception.DuplicateIdentificacionException;
import com.banco.clientepersona.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
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
public class ClienteService1 {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        // Validar si ya existe la identificación
        if (clienteRepository.existsByPersonaIdentificacion(clienteDTO.getIdentificacion())) {
            throw new DuplicateIdentificacionException(
                "Ya existe un cliente con la identificación: " + clienteDTO.getIdentificacion());
        }

        // Mapear DTO a entidades
        Persona persona = modelMapper.map(clienteDTO, Persona.class);
        Cliente cliente = modelMapper.map(clienteDTO, Cliente.class);
        cliente.setPersona(persona);

        // Guardar
        Cliente clienteGuardado = clienteRepository.save(cliente);
        
        return modelMapper.map(clienteGuardado, ClienteDTO.class);
    }

    @Transactional(readOnly = true)
    public ClienteDTO obtenerCliente(String clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ClienteNotFoundException(
                "Cliente no encontrado con ID: " + clienteId));
        
        return modelMapper.map(cliente, ClienteDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ClienteDTO> listarClientes() {
        return clienteRepository.findAll().stream()
            .map(cliente -> modelMapper.map(cliente, ClienteDTO.class))
            .collect(Collectors.toList());
    }

    @Transactional
    public ClienteDTO actualizarCliente(String clienteId, ClienteDTO clienteDTO) {
        Cliente clienteExistente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new ClienteNotFoundException(
                "Cliente no encontrado con ID: " + clienteId));

        // Actualizar persona
        Persona persona = clienteExistente.getPersona();
        persona.setNombre(clienteDTO.getNombre());
        persona.setGenero(clienteDTO.getGenero());
        persona.setEdad(clienteDTO.getEdad());
        persona.setDireccion(clienteDTO.getDireccion());
        persona.setTelefono(clienteDTO.getTelefono());

        // Actualizar cliente
        clienteExistente.setContrasena(clienteDTO.getContrasena());
        clienteExistente.setEstado(clienteDTO.getEstado());

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);
        
        return modelMapper.map(clienteActualizado, ClienteDTO.class);
    }

    @Transactional
    public void eliminarCliente(String clienteId) {
        if (!clienteRepository.existsById(clienteId)) {
            throw new ClienteNotFoundException(
                "Cliente no encontrado con ID: " + clienteId);
        }
        clienteRepository.deleteById(clienteId);
    }
}