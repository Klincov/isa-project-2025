package com.example.demo.dto;

public record PlaybackDto(
        boolean available,
        String serverNow,
        String scheduledAt,
        long startOffsetSec
) {}
