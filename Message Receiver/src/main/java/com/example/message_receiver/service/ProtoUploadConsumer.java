package com.example.message_receiver.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.example.events.UploadEventProto;

@Service
public class ProtoUploadConsumer {

    @RabbitListener(queues = "proto-upload-queue")
    public void receive(byte[] payload) {
        try {
            UploadEventProto event = UploadEventProto.parseFrom(payload);
            System.out.println("PROTO Consumer got: " + event.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
