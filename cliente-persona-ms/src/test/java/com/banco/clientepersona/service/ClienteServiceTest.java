package com.banco.clientepersona.service;

import com.banco.clientepersona.dto.ClienteDTO;
import com.banco.clientepersona.entity.Cliente;
import com.banco.clientepersona.entity.Persona;
import com.banco.clientepersona.event.ClienteEventPublisher;
import com.banco.clientepersona.exception.ClienteNotFoundException;
import com.banco.clientepersona.exception.DuplicateIdentificacionException;
import com.banco.clientepersona.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Cliente")
public class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ClienteEventPublisher eventPublisher;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteDTO clienteDTO;
    private Cliente cliente;
    private Persona persona;

    @BeforeEach
    void setUp() {
        // Configurar datos de prueba
        clienteDTO = new ClienteDTO();
        clienteDTO.setClienteId("1");
        clienteDTO.setContrasena("1234");
        clienteDTO.setEstado(true);
        clienteDTO.setNombre("Jose Lema");
        clienteDTO.setGenero("Masculino");
        clienteDTO.setEdad(30);
        clienteDTO.setIdentificacion("1234567890");
        clienteDTO.setDireccion("Otavalo sn y principal");
        clienteDTO.setTelefono("098254785");

        persona = new Persona();
        persona.setId(1L);
        persona.setNombre("Jose Lema");
        persona.setGenero("Masculino");
        persona.setEdad(30);
        persona.setIdentificacion("1234567890");
        persona.setDireccion("Otavalo sn y principal");
        persona.setTelefono("098254785");

        cliente = new Cliente();
        cliente.setClienteId("1");
        cliente.setContrasena("1234");
        cliente.setEstado(true);
        cliente.setPersona(persona);
    }

    @Nested
    @DisplayName("Pruebas para listarClientes()")
    class ListarClientesTests {

        @Test
        @DisplayName("Debe retornar lista de clientes cuando existen")
        void testListarClientes_ExistenClientes() {
            // Given
            List<Cliente> clientes = Arrays.asList(cliente);
            when(clienteRepository.findAll()).thenReturn(clientes);
            when(modelMapper.map(any(Cliente.class), eq(ClienteDTO.class))).thenReturn(clienteDTO);

            // When
            List<ClienteDTO> resultado = clienteService.listarClientes();

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("1", resultado.get(0).getClienteId());
            assertEquals("Jose Lema", resultado.get(0).getNombre());
            
            verify(clienteRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay clientes")
        void testListarClientes_NoHayClientes() {
            // Given
            when(clienteRepository.findAll()).thenReturn(Arrays.asList());

            // When
            List<ClienteDTO> resultado = clienteService.listarClientes();

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            
            verify(clienteRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Pruebas para obtenerCliente()")
    class ObtenerClienteTests {

        @Test
        @DisplayName("Debe retornar cliente cuando existe el ID")
        void testObtenerCliente_CuandoExiste() {
            // Given
            when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
            when(modelMapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

            // When
            ClienteDTO resultado = clienteService.obtenerCliente("1");

            // Then
            assertNotNull(resultado);
            assertEquals("1", resultado.getClienteId());
            assertEquals("Jose Lema", resultado.getNombre());
            
            verify(clienteRepository, times(1)).findById("1");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el cliente no existe")
        void testObtenerCliente_CuandoNoExiste() {
            // Given
            when(clienteRepository.findById("999")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ClienteNotFoundException.class, () -> {
                clienteService.obtenerCliente("999");
            });
            
            verify(clienteRepository, times(1)).findById("999");
        }
    }

    @Nested
    @DisplayName("Pruebas para crearCliente()")
    class CrearClienteTests {

        @Test
        @DisplayName("Debe crear cliente exitosamente")
        void testCrearCliente_Exitoso() {
            // Given
            when(clienteRepository.existsByPersonaIdentificacion(clienteDTO.getIdentificacion()))
                .thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
            when(modelMapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

            // When
            ClienteDTO resultado = clienteService.crearCliente(clienteDTO);

            // Then
            assertNotNull(resultado);
            assertEquals("1", resultado.getClienteId());
            
            verify(clienteRepository, times(1)).save(any(Cliente.class));
            verify(eventPublisher, times(1)).publishClienteCreado(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la identificación ya existe")
        void testCrearCliente_IdentificacionDuplicada() {
            // Given
            when(clienteRepository.existsByPersonaIdentificacion(clienteDTO.getIdentificacion()))
                .thenReturn(true);

            // When & Then
            assertThrows(DuplicateIdentificacionException.class, () -> {
                clienteService.crearCliente(clienteDTO);
            });
            
            verify(clienteRepository, never()).save(any(Cliente.class));
            verify(eventPublisher, never()).publishClienteCreado(anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Pruebas para actualizarCliente()")
    class ActualizarClienteTests {

        @Test
        @DisplayName("Debe actualizar cliente exitosamente")
        void testActualizarCliente_Exitoso() {
            // Given
            when(clienteRepository.findById("1")).thenReturn(Optional.of(cliente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);
            when(modelMapper.map(cliente, ClienteDTO.class)).thenReturn(clienteDTO);

            // When
            ClienteDTO resultado = clienteService.actualizarCliente("1", clienteDTO);

            // Then
            assertNotNull(resultado);
            assertEquals("1", resultado.getClienteId());
            
            verify(clienteRepository, times(1)).save(any(Cliente.class));
            verify(eventPublisher, times(1)).publishClienteActualizado(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el cliente no existe")
        void testActualizarCliente_ClienteNoExiste() {
            // Given
            when(clienteRepository.findById("999")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(ClienteNotFoundException.class, () -> {
                clienteService.actualizarCliente("999", clienteDTO);
            });
            
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para eliminarCliente()")
    class EliminarClienteTests {

        @Test
        @DisplayName("Debe eliminar cliente exitosamente")
        void testEliminarCliente_Exitoso() {
            // Given
            when(clienteRepository.existsById("1")).thenReturn(true);
            doNothing().when(clienteRepository).deleteById("1");

            // When
            clienteService.eliminarCliente("1");

            // Then
            verify(clienteRepository, times(1)).existsById("1");
            verify(clienteRepository, times(1)).deleteById("1");
            verify(eventPublisher, times(1)).publishClienteEliminado("1");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el cliente no existe")
        void testEliminarCliente_ClienteNoExiste() {
            // Given
            when(clienteRepository.existsById("999")).thenReturn(false);

            // When & Then
            assertThrows(ClienteNotFoundException.class, () -> {
                clienteService.eliminarCliente("999");
            });
            
            verify(clienteRepository, never()).deleteById(anyString());
        }
    }
}