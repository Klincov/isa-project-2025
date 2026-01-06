package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_VIDEO_SIZE = 200 * 1024 * 1024; // 200MB
    private static final Path VIDEO_DIR = Paths.get("uploads/videos");
    private static final Path THUMB_DIR = Paths.get("uploads/thumbnails");

    public String saveVideo(MultipartFile file) throws IOException {
        if (!file.getContentType().equals("video/mp4")) {
            throw new IllegalArgumentException("Video mora biti mp4");
        }

        if (file.getSize() > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException("Video prevelik");
        }

        Files.createDirectories(VIDEO_DIR);

        String filename = UUID.randomUUID() + ".mp4";
        Path target = VIDEO_DIR.resolve(filename);

        file.transferTo(target);

        return target.toString();
    }

    public String saveThumbnail(MultipartFile file) throws IOException {
        Files.createDirectories(THUMB_DIR);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path target = THUMB_DIR.resolve(filename);

        file.transferTo(target);

        return target.toString();
    }

    public void deleteFileIfExists(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException ignored) {}
    }
}
