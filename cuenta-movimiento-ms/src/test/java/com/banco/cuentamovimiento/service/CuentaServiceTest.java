package com.banco.cuentamovimiento.service;

import com.banco.cuentamovimiento.dto.CuentaDTO;
import com.banco.cuentamovimiento.entity.Cuenta;
import com.banco.cuentamovimiento.entity.ClienteLocal;
import com.banco.cuentamovimiento.exception.CuentaNotFoundException;
import com.banco.cuentamovimiento.repository.ClienteLocalRepository;
import com.banco.cuentamovimiento.repository.CuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Cuenta")
public class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private ClienteLocalRepository clienteLocalRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CuentaService cuentaService;

    private CuentaDTO cuentaDTO;
    private Cuenta cuenta;
    private ClienteLocal clienteLocal;

    @BeforeEach
    void setUp() {
        cuentaDTO = new CuentaDTO();
        cuentaDTO.setNumeroCuenta("478758");
        cuentaDTO.setTipoCuenta("Ahorros");
        cuentaDTO.setSaldoInicial(new BigDecimal("2000"));
        cuentaDTO.setSaldoDisponible(new BigDecimal("2000"));
        cuentaDTO.setEstado(true);
        cuentaDTO.setClienteid("1");

        cuenta = new Cuenta();
        cuenta.setNumeroCuenta("478758");
        cuenta.setTipoCuenta("Ahorros");
        cuenta.setSaldoInicial(new BigDecimal("2000"));
        cuenta.setSaldoDisponible(new BigDecimal("2000"));
        cuenta.setEstado(true);
        cuenta.setClienteid("1");
        
        clienteLocal = new ClienteLocal();
        clienteLocal.setClienteId("1");
        clienteLocal.setNombre("Cliente Test");
        clienteLocal.setActivo(true);
    }

    @Nested
    @DisplayName("Pruebas para crearCuenta()")
    class CrearCuentaTests {

        @Test
        @DisplayName("Debe crear cuenta exitosamente cuando el cliente existe")
        void testCrearCuenta_Exitoso() {
            // Given - Simular que el cliente EXISTE
            when(cuentaRepository.existsByNumeroCuenta("478758")).thenReturn(false);
            when(clienteLocalRepository.findById("1")).thenReturn(Optional.of(clienteLocal)); // Cliente existe
            when(modelMapper.map(cuentaDTO, Cuenta.class)).thenReturn(cuenta);
            when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);
            when(modelMapper.map(cuenta, CuentaDTO.class)).thenReturn(cuentaDTO);

            // When
            CuentaDTO resultado = cuentaService.crearCuenta(cuentaDTO);

            // Then
            assertNotNull(resultado);
            assertEquals("478758", resultado.getNumeroCuenta());
            assertEquals("1", resultado.getClienteid());
            
            verify(cuentaRepository, times(1)).save(any(Cuenta.class));
            verify(clienteLocalRepository, times(1)).findById("1");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el número de cuenta ya existe")
        void testCrearCuenta_NumeroCuentaDuplicado() {
            // Given
            when(cuentaRepository.existsByNumeroCuenta("478758")).thenReturn(true);

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                cuentaService.crearCuenta(cuentaDTO);
            });
            
            verify(cuentaRepository, never()).save(any(Cuenta.class));
            verify(clienteLocalRepository, never()).findById(anyString());
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el cliente no existe localmente")
        void testCrearCuenta_ClienteNoExiste() {
            // Given - Simular que el cliente NO existe
            when(cuentaRepository.existsByNumeroCuenta("478758")).thenReturn(false);
            when(clienteLocalRepository.findById("1")).thenReturn(Optional.empty()); // Cliente NO existe
            
            // NOTA: No simulamos nada más porque la excepción se lanza antes

            // When & Then
            assertThrows(RuntimeException.class, () -> {
                cuentaService.crearCuenta(cuentaDTO);
            });
            
            verify(cuentaRepository, never()).save(any(Cuenta.class));
            verify(clienteLocalRepository, times(1)).findById("1");
        }
    }

    @Nested
    @DisplayName("Pruebas para obtenerCuenta()")
    class ObtenerCuentaTests {

        @Test
        @DisplayName("Debe retornar cuenta cuando existe")
        void testObtenerCuenta_Existe() {
            // Given
            when(cuentaRepository.findById("478758")).thenReturn(Optional.of(cuenta));
            when(modelMapper.map(cuenta, CuentaDTO.class)).thenReturn(cuentaDTO);

            // When
            CuentaDTO resultado = cuentaService.obtenerCuenta("478758");

            // Then
            assertNotNull(resultado);
            assertEquals("478758", resultado.getNumeroCuenta());
            
            verify(cuentaRepository, times(1)).findById("478758");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la cuenta no existe")
        void testObtenerCuenta_NoExiste() {
            // Given
            when(cuentaRepository.findById("999")).thenReturn(Optional.empty());

            // When & Then
            assertThrows(CuentaNotFoundException.class, () -> {
                cuentaService.obtenerCuenta("999");
            });
            
            verify(cuentaRepository, times(1)).findById("999");
        }
    }

    @Nested
    @DisplayName("Pruebas para listarCuentasPorCliente()")
    class ListarCuentasPorClienteTests {

        @Test
        @DisplayName("Debe retornar cuentas del cliente")
        void testListarCuentasPorCliente() {
            // Given
            List<Cuenta> cuentas = Arrays.asList(cuenta);
            when(cuentaRepository.findByClienteid("1")).thenReturn(cuentas);
            when(modelMapper.map(any(Cuenta.class), eq(CuentaDTO.class))).thenReturn(cuentaDTO);

            // When
            List<CuentaDTO> resultado = cuentaService.listarCuentasPorCliente("1");

            // Then
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("478758", resultado.get(0).getNumeroCuenta());
            
            verify(cuentaRepository, times(1)).findByClienteid("1");
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando el cliente no tiene cuentas")
        void testListarCuentasPorCliente_SinCuentas() {
            // Given
            when(cuentaRepository.findByClienteid("2")).thenReturn(Arrays.asList());

            // When
            List<CuentaDTO> resultado = cuentaService.listarCuentasPorCliente("2");

            // Then
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
            
            verify(cuentaRepository, times(1)).findByClienteid("2");
        }
    }
}