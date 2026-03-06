package com.banco.clientepersona.event;

import com.banco.clientepersona.config.RabbitMQConfig;
import com.banco.clientepersona.dto.ClienteEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author user
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishClienteCreado(String clienteId, String nombre, String identificacion) {
        ClienteEventDTO evento = new ClienteEventDTO(
            clienteId,
            nombre,
            identificacion,
            "CREADO",
            System.currentTimeMillis()
        );
        
        log.info("Publicando evento cliente creado: {}", evento);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.CLIENTE_CREADO_ROUTING_KEY,
            evento
        );
    }

    public void publishClienteActualizado(String clienteId, String nombre, String identificacion) {
        ClienteEventDTO evento = new ClienteEventDTO(
            clienteId,
            nombre,
            identificacion,
            "ACTUALIZADO",
            System.currentTimeMillis()
        );
        
        log.info("Publicando evento cliente actualizado: {}", evento);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.CLIENTE_CREADO_ROUTING_KEY,
            evento
        );
    }

    public void publishClienteEliminado(String clienteId) {
        ClienteEventDTO evento = new ClienteEventDTO(
            clienteId,
            null,
            null,
            "ELIMINADO",
            System.currentTimeMillis()
        );
        
        log.info("Publicando evento cliente eliminado: {}", evento);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE,
            RabbitMQConfig.CLIENTE_CREADO_ROUTING_KEY,
            evento
        );
    }
}