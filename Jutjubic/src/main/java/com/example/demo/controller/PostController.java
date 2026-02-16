package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Post;
import com.example.demo.events.EventPublisher;
import com.example.demo.events.UploadEvent;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.security.AppUserDetails;
import com.example.demo.service.PostService;
import com.example.events.UploadEventProto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/upload-video")
public class PostController {

    private final PostService postService;
    private final EventPublisher eventPublisher;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> createPost(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<String> tags,
            @RequestParam MultipartFile video,
            @RequestParam MultipartFile thumbnail,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String scheduledAt,
            Authentication authentication
    ) throws IOException {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Morate biti prijavljeni da biste postavili video.");
        }

        if (video.isEmpty()) {
            throw new BadRequestException("Video fajl je obavezan.");
        }

        String thumbnailContentType = thumbnail.getContentType();
        if (thumbnailContentType == null ||
                (!thumbnailContentType.equals("image/jpeg") && !thumbnailContentType.equals("image/png"))) {
            throw new BadRequestException("Thumbnail mora biti JPG ili PNG slika.");
        }

        String videoContentType = video.getContentType();
        if (videoContentType == null || !videoContentType.equals("video/mp4")) {
            throw new BadRequestException("Video mora biti u MP4 formatu.");
        }

        LocalDateTime scheduled = null;
        if (scheduledAt != null && !scheduledAt.isBlank()) {
            scheduled = LocalDateTime.parse(scheduledAt);
            if (scheduled.isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Zakazano vreme mora biti u buducnosti.");
            }
        }

        User user = ((AppUserDetails) authentication.getPrincipal()).getUser();

        try {
            Post post = postService.createPost(
                    title, description, tags, video, thumbnail, user, lat, lon,scheduled
            );
            UploadEvent event = new UploadEvent(
                    post.getId(),
                    post.getTitle(),
                    user.getUsername(),
                    video.getSize(),
                    post.getCreatedAt()
            );
            eventPublisher.sendJsonEvent(event);
            eventPublisher.sendProtoEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(post);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }

    }


}

