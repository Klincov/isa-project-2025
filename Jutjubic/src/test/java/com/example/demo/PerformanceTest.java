package com.example.demo;

import com.example.demo.events.UploadEvent;
import com.example.events.UploadEventProto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private static final int TEST_SIZE = 50;

    @Test
    public void compareJsonAndProtobuf() throws Exception {

        List<UploadEvent> events = generateEvents();

        testJson(events);
        testProtobuf(events);
    }

    private List<UploadEvent> generateEvents() {

        List<UploadEvent> events = new ArrayList<>();

        for (int i = 0; i < TEST_SIZE; i++) {
            events.add(new UploadEvent(
                    (long) i,
                    "Video title " + i,
                    "user" + i,
                    10_000_000L + i,
                    LocalDateTime.now()
            ));
        }

        return events;
    }

    private void testJson(List<UploadEvent> events) throws Exception {

        long totalSerialization = 0;
        long totalDeserialization = 0;
        long totalSize = 0;

        for (UploadEvent event : events) {

            long start = System.nanoTime();
            byte[] bytes = objectMapper.writeValueAsBytes(event);
            totalSerialization += (System.nanoTime() - start);

            totalSize += bytes.length;

            start = System.nanoTime();
            objectMapper.readValue(bytes, UploadEvent.class);
            totalDeserialization += (System.nanoTime() - start);
        }

        System.out.println("----- JSON -----");
        System.out.println("Avg serialization (ns): " + totalSerialization / TEST_SIZE);
        System.out.println("Avg deserialization (ns): " + totalDeserialization / TEST_SIZE);
        System.out.println("Avg size (bytes): " + totalSize / TEST_SIZE);
    }

    private void testProtobuf(List<UploadEvent> events) throws Exception {

        long totalSerialization = 0;
        long totalDeserialization = 0;
        long totalSize = 0;

        for (UploadEvent event : events) {

            UploadEventProto proto = UploadEventProto.newBuilder()
                    .setVideoId(event.getVideoId())
                    .setTitle(event.getTitle())
                    .setAuthorUsername(event.getAuthorUsername())
                    .setVideoSize(event.getVideoSize())
                    .setCreatedAt(event.getCreatedAt().toString())
                    .build();

            long start = System.nanoTime();
            byte[] bytes = proto.toByteArray();
            totalSerialization += (System.nanoTime() - start);

            totalSize += bytes.length;

            start = System.nanoTime();
            UploadEventProto.parseFrom(bytes);
            totalDeserialization += (System.nanoTime() - start);
        }

        System.out.println("----- PROTOBUF -----");
        System.out.println("Avg serialization (ns): " + totalSerialization / TEST_SIZE);
        System.out.println("Avg deserialization (ns): " + totalDeserialization / TEST_SIZE);
        System.out.println("Avg size (bytes): " + totalSize / TEST_SIZE);
    }
}
