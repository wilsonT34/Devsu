package com.banco.cuentamovimiento.consumer;

import com.banco.cuentamovimiento.dto.ClienteEventDTO;
import com.banco.cuentamovimiento.entity.ClienteLocal;
import com.banco.cuentamovimiento.repository.ClienteLocalRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author user
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventConsumer {

    private final ClienteLocalRepository clienteLocalRepository;
    
    @PostConstruct
    public void init() {
        log.info("==========================================");
        log.info("CONSUMER DE RABBITMQ INICIALIZADO");
        log.info("Esperando eventos en cola: cliente.creado.queue");
        log.info("==========================================");
    }

    
    @RabbitListener(queues = "cliente.creado.queue")
    @Transactional
    public void handleClienteEvent(ClienteEventDTO evento) {
        log.info("Evento recibido: {}", evento);
        
        switch (evento.getEvento()) {
            case "CREADO":
                handleClienteCreado(evento);
                break;
            case "ACTUALIZADO":
                handleClienteActualizado(evento);
                break;
            case "ELIMINADO":
                handleClienteEliminado(evento);
                break;
            default:
                log.warn("Evento desconocido: {}", evento.getEvento());
        }
    }

    private void handleClienteCreado(ClienteEventDTO evento) {
        ClienteLocal cliente = new ClienteLocal();
        cliente.setClienteId(evento.getClienteId());
        cliente.setNombre(evento.getNombre());
        cliente.setIdentificacion(evento.getIdentificacion());
        cliente.setActivo(true);
        
        clienteLocalRepository.save(cliente);
        log.info("Cliente almacenado localmente: {}", evento.getClienteId());
    }

    private void handleClienteActualizado(ClienteEventDTO evento) {
        clienteLocalRepository.findById(evento.getClienteId()).ifPresentOrElse(
            cliente -> {
                cliente.setNombre(evento.getNombre());
                cliente.setIdentificacion(evento.getIdentificacion());
                clienteLocalRepository.save(cliente);
                log.info("Cliente actualizado localmente: {}", evento.getClienteId());
            },
            () -> log.warn("Cliente no encontrado para actualizar: {}", evento.getClienteId())
        );
    }

    private void handleClienteEliminado(ClienteEventDTO evento) {
        clienteLocalRepository.findById(evento.getClienteId()).ifPresent(
            cliente -> {
                cliente.setActivo(false);
                clienteLocalRepository.save(cliente);
                log.info("Cliente desactivado localmente: {}", evento.getClienteId());
            }
        );
    }
}