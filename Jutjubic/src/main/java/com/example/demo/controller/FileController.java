package com.example.demo.controller;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.FileStorageService;
import com.example.demo.service.PostFeedService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private final PostFeedService postFeedService;

    public FileController(PostRepository postRepository,FileStorageService fileStorageService,PostFeedService postFeedService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
        this.postFeedService = postFeedService;
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

    @GetMapping(value = "/video/{postId}", produces = "video/mp4")
    public ResponseEntity<ResourceRegion> video(@PathVariable Long postId,
                                                @RequestHeader HttpHeaders headers,
                                                Authentication auth) throws Exception {
        Post p = postRepository.findById(postId).orElseThrow();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        if (!postFeedService.canSeePost(p, auth, now)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Path path = Paths.get(p.getVideoPath());
        Resource res = new FileSystemResource(path);
        if (!res.exists()) return ResponseEntity.notFound().build();

        long contentLength = res.contentLength();
        long chunkSize = 1024L * 1024L; // 1MB

        HttpRange range = headers.getRange().stream().findFirst().orElse(null);

        ResourceRegion region;
        if (range == null) {
            region = new ResourceRegion(res, 0, Math.min(chunkSize, contentLength));
        } else {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(chunkSize, end - start + 1);
            region = new ResourceRegion(res, start, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.valueOf("video/mp4"))
                .body(region);
    }

}
