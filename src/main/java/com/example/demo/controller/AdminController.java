package com.example.demo.controller;

import com.example.demo.dto.AssignComplaintRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpsertRequest;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ComplaintService complaintService;
    private final UserManagementService userManagementService;

    public AdminController(ComplaintService complaintService, UserManagementService userManagementService) {
        this.complaintService = complaintService;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/complaints")
    public List<ComplaintResponse> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @PutMapping("/complaints/{complaintId}/assign")
    public ComplaintResponse assignComplaint(
        @PathVariable Long complaintId,
        @Valid @RequestBody AssignComplaintRequest request
    ) {
        return complaintService.assignComplaint(complaintId, request);
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserUpsertRequest request) {
        return userManagementService.createUser(request);
    }

    @PutMapping("/users/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpsertRequest request) {
        return userManagementService.updateUser(userId, request);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        userManagementService.deleteUser(userId);
    }
}
