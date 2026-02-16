package com.example.demo.controller;

import com.example.demo.service.PopularVideosQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PopularVidController {

    private final PopularVideosQueryService popularVideosQueryService;

    @GetMapping("/api/home/popular")
    public List<PopularVideosQueryService.Item> popular(Authentication auth) {
        if (auth == null || "anonymousUser".equals(auth.getName())) return List.of();
        return popularVideosQueryService.latestTop3();
    }
}
