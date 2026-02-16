package com.example.demo.events;

import com.example.events.UploadEventProto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE = "upload.exchange";
    private static final String ROUTING_JSON = "upload.json";
    private static final String ROUTING_PROTO = "upload.proto";

    public void sendJsonEvent(UploadEvent event) {

        try {
            byte[] payload = objectMapper.writeValueAsBytes(event);

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_JSON, payload);

        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }

    public void sendProtoEvent(UploadEvent event) {

        UploadEventProto proto = UploadEventProto.newBuilder()
                .setVideoId(event.getVideoId())
                .setTitle(event.getTitle())
                .setAuthorUsername(event.getAuthorUsername())
                .setVideoSize(event.getVideoSize())
                .setCreatedAt(event.getCreatedAt().toString())
                .build();

        byte[] payload = proto.toByteArray();

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_PROTO, payload);

    }
}
