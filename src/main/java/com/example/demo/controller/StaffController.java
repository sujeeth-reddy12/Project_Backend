package com.example.demo.controller;

import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.UpdateComplaintStatusRequest;
import com.example.demo.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final ComplaintService complaintService;

    public StaffController(ComplaintService complaintService) {
        this.complaintService = complaintService;
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
}
