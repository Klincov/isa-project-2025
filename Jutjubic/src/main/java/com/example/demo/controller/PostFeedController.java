package com.example.demo.controller;

import com.example.demo.dto.PlaybackDto;
import com.example.demo.dto.PostDto;
import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.PostFeedService;
import com.example.demo.service.ViewsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostFeedController {

    private final PostFeedService postFeedService;
    private final PostRepository postRepository;
    private final ViewsService viewsService;

    public PostFeedController(PostRepository postRepository, PostFeedService postFeedService,ViewsService viewsService) {
        this.postRepository = postRepository;
        this.postFeedService = postFeedService;
        this.viewsService = viewsService;
    }

    @GetMapping
    public List<PostDto.PostListItemDto> list(Authentication auth) {
        LocalDateTime now = LocalDateTime.now();

        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(p -> postFeedService.canSeePost(p, auth, now))
                .map(p -> new PostDto.PostListItemDto(
                        p.getId(),
                        p.getTitle(),
                        "/api/files/thumbnail/" + p.getId(),
                        p.getCreatedAt()
                ))
                .toList();
    }


    @PostMapping("/{id}/view")
    public void registerView(@PathVariable Long id) {
        viewsService.increment(id);
    }

    @GetMapping("/{id}/views/state")
    public Map<String, Long> getViewsState(@PathVariable Long id) {
        return viewsService.getState(id);
    }

    @PostMapping("/{id}/views/merge")
    public void mergeViews(@PathVariable Long id, @RequestBody Map<String, Long> incoming) {
        viewsService.mergeState(id, incoming);
    }



    @GetMapping("/{id}")
    public PostDto.PostDetailsDto details(@PathVariable Long id, Authentication auth) {
        Post p = postRepository.findById(id).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        if (!postFeedService.canSeePost(p, auth, now)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        boolean available = postFeedService.isAvailable(p, now);

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
                p.getCreatedAt(),
                viewsService.getTotal(id),
                p.getScheduledAt(),
                available
        );
    }

    @GetMapping("/{id}/playback")
    public PlaybackDto playback(@PathVariable Long id, Authentication auth) {
        Post p = postRepository.findById(id).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        if (!postFeedService.canSeePost(p, auth, now)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        boolean available = postFeedService.isAvailable(p, now);
        long offsetSec = postFeedService.startOffsetSec(p, now);

        return new PlaybackDto(
                available,
                now.toString(),
                p.getScheduledAt() == null ? null : p.getScheduledAt().toString(),
                offsetSec
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
