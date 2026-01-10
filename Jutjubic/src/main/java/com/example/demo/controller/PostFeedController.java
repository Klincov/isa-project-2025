package com.example.demo.controller;

import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostFeedController {

    private final PostRepository postRepository;

    public PostFeedController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @GetMapping
    public List<PostDto.PostListItemDto> list(Authentication auth) {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(p -> new PostDto.PostListItemDto(
                        p.getId(),
                        p.getTitle(),
                        "/api/files/thumbnail/" + p.getId(),
                        p.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public PostDto.PostDetailsDto details(@PathVariable Long id) {
        Post p = postRepository.findById(id).orElseThrow();
        return new PostDto.PostDetailsDto(
                p.getId(),
                p.getTitle(),
                p.getDescription(),
                p.getTags(),
                "/api/files/video/" + p.getId(),
                "/api/files/thumbnail/" + p.getId(),
                p.getLikesCount(),
                p.getLatitude(),
                p.getLongitude(),
                p.getCreatedAt()
        );
    }

    @PostMapping("/{id}/like")
    public PostDto.ApiMessage like(@PathVariable Long id) {
        Post p = postRepository.findById(id).orElseThrow();
        p.setLikesCount(p.getLikesCount() + 1);
        postRepository.save(p);
        return new PostDto.ApiMessage("Liked");
    }
}
