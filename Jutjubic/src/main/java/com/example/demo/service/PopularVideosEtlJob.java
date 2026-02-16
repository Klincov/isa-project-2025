package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularVideosEtlJob {

    private final JdbcTemplate jdbc;

    @Value("${app.etl.lock-id:424242}")
    private long lockId;

    @Scheduled(cron = "${app.etl.cron:0 0 2 * * *}")
    @Transactional
    public void runDaily() {
        Boolean locked = jdbc.queryForObject("SELECT pg_try_advisory_lock(?)", Boolean.class, lockId);
        if (locked == null || !locked) return;

        try {
            LocalDate day = LocalDate.now().minusDays(1);
            LocalDate prevDay = day.minusDays(1);
            LocalDate start = day.minusDays(6);

            List<PostTotal> totals = jdbc.query("""
                SELECT post_id, COALESCE(SUM(cnt),0) AS total
                FROM video_view_gcounter
                GROUP BY post_id
            """, (rs, i) -> new PostTotal(rs.getLong("post_id"), rs.getLong("total")));

            for (PostTotal pt : totals) {
                jdbc.update("""
                    INSERT INTO video_view_total_snapshot(post_id, day, total)
                    VALUES (?, ?, ?)
                    ON CONFLICT (post_id, day) DO UPDATE SET total = EXCLUDED.total
                """, pt.postId(), Date.valueOf(day), pt.total());
            }

            jdbc.update("""
                INSERT INTO video_view_daily(post_id, day, views)
                SELECT s1.post_id,
                       s1.day,
                       GREATEST(s1.total - COALESCE(s0.total,0), 0) AS views
                FROM video_view_total_snapshot s1
                LEFT JOIN video_view_total_snapshot s0
                       ON s0.post_id = s1.post_id AND s0.day = ?
                WHERE s1.day = ?
                ON CONFLICT (post_id, day) DO UPDATE SET views = EXCLUDED.views
            """, Date.valueOf(prevDay), Date.valueOf(day));

            List<ScoreRow> top3 = jdbc.query("""
                SELECT d.post_id,
                       SUM(d.views * (7 - (?::date - d.day)))::bigint AS score
                FROM video_view_daily d
                WHERE d.day BETWEEN ? AND ?
                GROUP BY d.post_id
                ORDER BY score DESC
                LIMIT 3
            """, (rs, i) -> new ScoreRow(rs.getLong("post_id"), rs.getLong("score")),
                    Date.valueOf(day), Date.valueOf(start), Date.valueOf(day));

            Long runId = jdbc.queryForObject("""
                INSERT INTO popular_videos_run(run_at)
                VALUES (?)
                RETURNING id
            """, Long.class, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            if (runId == null) throw new IllegalStateException("popular_videos_run insert failed");

            int rank = 1;
            for (ScoreRow s : top3) {
                jdbc.update("""
                    INSERT INTO popular_videos_run_item(run_id, rank, post_id, score)
                    VALUES (?, ?, ?, ?)
                """, runId, rank++, s.postId(), s.score());
            }
        } finally {
            jdbc.queryForObject("SELECT pg_advisory_unlock(?)", Boolean.class, lockId);
        }
    }

    private record PostTotal(long postId, long total) {}
    private record ScoreRow(long postId, long score) {}
}
