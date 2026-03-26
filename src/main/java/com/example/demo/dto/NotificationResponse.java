package com.example.demo.dto;

import com.example.demo.entity.NotificationRecord;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String event;
    private String message;
    private boolean read;
    private Long complaintId;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(NotificationRecord notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setEvent(notification.getEvent());
        response.setMessage(notification.getMessage());
        response.setRead(notification.isRead());
        response.setComplaintId(notification.getComplaint() != null ? notification.getComplaint().getId() : null);
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Long getComplaintId() {
        return complaintId;
    }

    public void setComplaintId(Long complaintId) {
        this.complaintId = complaintId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
