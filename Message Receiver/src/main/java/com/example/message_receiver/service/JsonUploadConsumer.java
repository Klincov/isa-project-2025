package com.example.message_receiver.service;

import com.example.message_receiver.dto.UploadEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class JsonUploadConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "json-upload-queue")
    public void receive(byte[] payload) {
        try {
            UploadEvent event = objectMapper.readValue(payload, UploadEvent.class);
            System.out.println("JSON Consumer got: " + event.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

