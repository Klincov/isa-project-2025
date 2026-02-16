package com.example.message_receiver.dto;

import java.time.LocalDateTime;

public class UploadEvent {

    private Long videoId;
    private String title;
    private String authorUsername;
    private long videoSize;
    private LocalDateTime createdAt;

    public UploadEvent(Long videoId, String title, String authorUsername, long videoSize, LocalDateTime createdAt) {
        this.videoId = videoId;
        this.title = title;
        this.authorUsername = authorUsername;
        this.videoSize = videoSize;
        this.createdAt = createdAt;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public long getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(long videoSize) {
        this.videoSize = videoSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
