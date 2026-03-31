package com.example.demo.controller;

import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserManagementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/data")
public class DataViewController {

    private final UserManagementService userManagementService;

    public DataViewController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userManagementService.getAllUsers();
    }
}
