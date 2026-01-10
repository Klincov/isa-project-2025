package com.example.demo.service;

import com.example.demo.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
