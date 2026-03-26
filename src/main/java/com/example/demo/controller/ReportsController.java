package com.example.demo.controller;

import com.example.demo.dto.SummaryResponse;
import com.example.demo.service.ComplaintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

    private final ComplaintService complaintService;

    public ReportsController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/summary")
    public SummaryResponse summary() {
        return complaintService.getSummary();
    }
}
