package com.example.demo.controller;

import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.UpdateComplaintStatusRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final ComplaintService complaintService;
    private final UserManagementService userManagementService;

    public StaffController(ComplaintService complaintService, UserManagementService userManagementService) {
        this.complaintService = complaintService;
        this.userManagementService = userManagementService;
    }

    @GetMapping("/{staffId}/complaints")
    public List<ComplaintResponse> getComplaintsForStaff(@PathVariable Long staffId) {
        return complaintService.getComplaintsForStaff(staffId);
    }

    @PutMapping("/complaints/{complaintId}/status")
    public ComplaintResponse updateComplaintStatus(
        @PathVariable Long complaintId,
        @Valid @RequestBody UpdateComplaintStatusRequest request
    ) {
        return complaintService.updateComplaintStatus(complaintId, request);
    }

    @GetMapping("/{staffId}/profile")
    public UserResponse getStaffProfile(@PathVariable Long staffId) {
        return userManagementService.getUserById(staffId);
    }

    @PutMapping("/{staffId}/availability")
    public UserResponse updateAvailability(@PathVariable Long staffId, @RequestParam boolean available) {
        return userManagementService.updateAvailability(staffId, available);
    }
}
