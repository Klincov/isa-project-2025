package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;

@Service
public class ViewsService {

    private final JdbcTemplate jdbc;
    private final String replicaId;

    public ViewsService(JdbcTemplate jdbc,
                        @Value("${app.replica-id}") String replicaId) {
        this.jdbc = jdbc;
        this.replicaId = replicaId;
    }

    @Transactional
    public void increment(Long postId) {
        jdbc.update("""
            INSERT INTO video_view_gcounter (post_id, replica_id, cnt)
            VALUES (?, ?, 1)
            ON CONFLICT (post_id, replica_id)
            DO UPDATE SET cnt = video_view_gcounter.cnt + 1
        """, postId, replicaId);
    }

    public long getTotal(Long postId) {
        Long sum = jdbc.queryForObject("""
            SELECT COALESCE(SUM(cnt), 0)
            FROM video_view_gcounter
            WHERE post_id = ?
        """, Long.class, postId);
        return sum == null ? 0L : sum;
    }

    public Map<String, Long> getState(Long postId) {
        return jdbc.query("""
            SELECT replica_id, cnt
            FROM video_view_gcounter
            WHERE post_id = ?
        """, rs -> {
            Map<String, Long> m = new java.util.HashMap<>();
            while (rs.next()) m.put(rs.getString("replica_id"), rs.getLong("cnt"));
            return m;
        }, postId);
    }

    @Transactional
    public void mergeState(Long postId, Map<String, Long> incoming) {
        incoming.forEach((rid, inc) -> jdbc.update("""
            INSERT INTO video_view_gcounter (post_id, replica_id, cnt)
            VALUES (?, ?, ?)
            ON CONFLICT (post_id, replica_id)
            DO UPDATE SET cnt = GREATEST(video_view_gcounter.cnt, EXCLUDED.cnt)
        """, postId, rid, inc));
    }
}


