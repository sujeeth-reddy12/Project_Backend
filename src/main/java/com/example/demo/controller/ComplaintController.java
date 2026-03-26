package com.example.demo.controller;

import com.example.demo.dto.ComplaintCreateRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.NotificationResponse;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final NotificationService notificationService;

    public ComplaintController(ComplaintService complaintService, NotificationService notificationService) {
        this.complaintService = complaintService;
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse createComplaint(@Valid @RequestBody ComplaintCreateRequest request) {
        return complaintService.createComplaint(request);
    }

    @GetMapping("/user/{userId}")
    public List<ComplaintResponse> getComplaintsForUser(@PathVariable Long userId) {
        return complaintService.getComplaintsForUser(userId);
    }

    @GetMapping("/user/{userId}/notifications")
    public List<NotificationResponse> getNotificationsForUser(@PathVariable Long userId) {
        return notificationService.getNotificationsForUser(userId);
    }
}
