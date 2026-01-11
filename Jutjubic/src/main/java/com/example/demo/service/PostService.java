package com.example.demo.service;

import com.example.demo.entity.AppUser;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private final PostTransactionService postTransactionService;

    public PostService(PostRepository postRepository,
                       FileStorageService fileStorageService, PostTransactionService postTransactionService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
        this.postTransactionService = postTransactionService;
    }

    public Post createPost(
            String title,
            String description,
            List<String> tags,
            MultipartFile video,
            MultipartFile thumbnail,
            AppUser author,
            Double lat,
            Double lon
    ) throws IOException, InterruptedException {
        String videoPath = null;
        String thumbnailPath = null;

        try {
            videoPath = fileStorageService.saveVideo(video);
            thumbnailPath = fileStorageService.saveThumbnail(thumbnail);

            Post post = new Post();
            post.setTitle(title);
            post.setDescription(description);
            post.setTags(tags);
            post.setVideoPath(videoPath);
            post.setThumbnailPath(thumbnailPath);
            post.setCreatedAt(LocalDateTime.now());
            post.setLatitude(lat);
            post.setLongitude(lon);
            post.setAuthor(author);
            post.setViewCount(0);
            post.setLikesCount(0);
            return postTransactionService.savePostToDatabase(post);

        } catch (Exception e) {
            System.out.println("Deleting files...");
            if (videoPath != null) fileStorageService.deleteFileIfExists(videoPath);
            if (thumbnailPath != null) fileStorageService.deleteFileIfExists(thumbnailPath);
            throw e;
        }

    }

}


