package com.example.message_receiver.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "upload.exchange";
    public static final String JSON_QUEUE = "json-upload-queue";
    public static final String PROTO_QUEUE = "proto-upload-queue";

    @Bean
    public DirectExchange uploadExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue jsonQueue() {
        return new Queue(JSON_QUEUE, true);
    }

    @Bean
    public Queue protoQueue() {
        return new Queue(PROTO_QUEUE, true);
    }

    @Bean
    public Binding jsonBinding(Queue jsonQueue, DirectExchange uploadExchange) {
        return BindingBuilder.bind(jsonQueue).to(uploadExchange).with("upload.json");
    }

    @Bean
    public Binding protoBinding(Queue protoQueue, DirectExchange uploadExchange) {
        return BindingBuilder.bind(protoQueue).to(uploadExchange).with("upload.proto");
    }
}

