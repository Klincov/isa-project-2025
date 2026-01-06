package com.example.demo.controller;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Post;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.security.AppUserDetails;
import com.example.demo.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/upload-video")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post createPost(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam List<String> tags,
            @RequestParam MultipartFile video,
            @RequestParam MultipartFile thumbnail,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            Authentication authentication
    ) throws IOException {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Morate biti prijavljeni da biste postavili video.");
        }

        if (video.isEmpty()) {
            throw new BadRequestException("Video fajl je obavezan.");
        }

        if (!video.getContentType().equals("video/mp4")) {
            throw new BadRequestException("Video mora biti u MP4 formatu.");
        }

        AppUser user = ((AppUserDetails) authentication.getPrincipal()).getUser();
        return postService.createPost(
                title, description, tags, video, thumbnail, user, lat, lon
        );
    }
}

