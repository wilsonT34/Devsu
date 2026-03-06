package com.banco.cuentamovimiento.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author user
 */

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "banco.exchange";
    public static final String CLIENTE_CREADO_QUEUE = "cliente.creado.queue";
    public static final String CLIENTE_CREADO_ROUTING_KEY = "cliente.creado";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue clienteCreadoQueue() {
        return new Queue(CLIENTE_CREADO_QUEUE);
    }

    @Bean
    public Binding clienteCreadoBinding() {
        return BindingBuilder
                .bind(clienteCreadoQueue())
                .to(exchange())
                .with(CLIENTE_CREADO_ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    
}
