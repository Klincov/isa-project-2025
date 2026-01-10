package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {

    public record PostListItemDto(Long id, String title, String thumbnailUrl, LocalDateTime createdAt) {}

    public record PostDetailsDto(Long id,
                                 String title,
                                 String description,
                                 List<String> tags,
                                 String videoUrl,
                                 String thumbnailUrl,
                                 long likesCount,
                                 Double latitude,
                                 Double longitude,
                                 LocalDateTime createdAt) {}

    public record ApiMessage(String message) {}
}

