package com.example.demo.controller;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.FileStorageService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    public FileController(PostRepository postRepository,FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/thumbnail/{postId}")
    public ResponseEntity<byte[]> thumbnail(@PathVariable Long postId) {
        Post p = postRepository.findById(postId).orElseThrow();
        byte[] image = fileStorageService.loadThumbnail(
                p.getThumbnailPath()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @GetMapping("/video/{postId}")
    public ResponseEntity<Resource> video(@PathVariable Long postId) {
        Post p = postRepository.findById(postId).orElseThrow();

        Path path = Paths.get(p.getVideoPath());
        Resource res = new FileSystemResource(path);

        if (!res.exists()) return ResponseEntity.notFound().build();

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(res);
    }
}
