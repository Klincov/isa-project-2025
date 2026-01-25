package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ViewsService {

    private final JdbcTemplate jdbc;
    private final String tableName;

    public ViewsService(
            JdbcTemplate jdbc,
            @Value("${app.views-table}") String tableName
    ) {
        this.jdbc = jdbc;
        this.tableName = tableName;
    }

    @Transactional
    public void Increment(Long postId) {

        jdbc.update("""
            INSERT INTO %s (post_id, views)
            VALUES (?, 1)
            ON CONFLICT (post_id)
            DO UPDATE SET views = %s.views + 1
        """.formatted(tableName, tableName), postId);
    }

    public Long Get(Long postId) {

        return jdbc.queryForObject(
                "SELECT views FROM " + tableName + " WHERE post_id = ?",
                Long.class,
                postId
        );
    }
}

