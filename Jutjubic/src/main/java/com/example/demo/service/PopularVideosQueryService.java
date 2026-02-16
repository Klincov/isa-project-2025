package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularVideosQueryService {

    private final JdbcTemplate jdbc;

    public List<Item> latestTop3() {
        Long runId = jdbc.queryForObject("""
            SELECT id
            FROM popular_videos_run
            ORDER BY run_at DESC
            LIMIT 1
        """, Long.class);

        if (runId == null) return List.of();

        return jdbc.query("""
            SELECT rank, post_id, score
            FROM popular_videos_run_item
            WHERE run_id = ?
            ORDER BY rank
        """, (rs, i) -> new Item(rs.getInt("rank"), rs.getLong("post_id"), rs.getLong("score")), runId);
    }

    public record Item(int rank, long postId, long score) {}
}
