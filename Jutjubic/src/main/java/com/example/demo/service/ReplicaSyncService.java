package com.example.demo.service;

import com.example.demo.entity.Post;
import com.example.demo.repository.PostRepository;
import com.example.demo.service.ViewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReplicaSyncService {

    private final RestTemplate restTemplate;
    private final ViewsService viewsService;
    private final PostRepository postRepository;

    @Value("${app.other-replicas}")
    private List<String> otherReplicas;

    public ReplicaSyncService(RestTemplate restTemplate, ViewsService viewsService, PostRepository postRepository) {
        this.restTemplate = restTemplate;
        this.viewsService = viewsService;
        this.postRepository = postRepository;
    }

    @Scheduled(fixedDelay = 5000)
    public void syncViews() {
        List<Post> posts = postRepository.findAll();

        for(Post post : posts) {
            for (String replicaUrl : otherReplicas) {
                try {
                    Map<String, Number> remoteState =
                            restTemplate.getForObject(
                                    replicaUrl + "/api/posts/{id}/views/state",
                                    Map.class,
                                    post.getId()
                            );

                    if (remoteState != null) {
                        Map<String, Long> normalized = new HashMap<>();
                        remoteState.forEach((rid, n) -> normalized.put(rid, n.longValue()));
                        viewsService.mergeState(post.getId(), normalized);
                    }

                }catch (Exception e) {
                System.out.println("Sync failed from {}"+ replicaUrl+ e);
            }

        }
        }
    }
}
