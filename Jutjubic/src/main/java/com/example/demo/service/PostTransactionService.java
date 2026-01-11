package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostTransactionService {

    private final PostRepository postRepository;
    
    public PostTransactionService(PostRepository postRepository){
        this.postRepository = postRepository;
    }
    @Transactional(timeout = 30)
    public Post savePostToDatabase(Post post){
        return postRepository.save(post);
    }
}
