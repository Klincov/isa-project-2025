package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.security.AppUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PostFeedService {
    private final PostRepository postRepository;

    public PostFeedService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    @Transactional
    public void registerView(Long postId) {
        postRepository.incrementViews(postId);
    }

    public boolean canSeePost(Post p, Authentication auth, LocalDateTime now) {
        boolean available = isAvailable(p, now);
        if (available) return true;

        Long viewerId = getViewerUserId(auth);
        return viewerId != null && p.getAuthor().getId().equals(viewerId);
    }

    public boolean isAvailable(Post p, LocalDateTime now) {
        return p.getScheduledAt() == null || !now.isBefore(p.getScheduledAt());
    }

    public long startOffsetSec(Post p, LocalDateTime now) {
        if (p.getScheduledAt() == null) return 0;
        if (now.isBefore(p.getScheduledAt())) return 0;
        long sec = Duration.between(p.getScheduledAt(), now).toSeconds();
        return Math.max(0, sec);
    }

    public Long getViewerUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof AppUserDetails aud) return aud.getUser().getId();
        return null;
    }
}
