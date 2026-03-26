package com.example.demo.service;

import com.example.demo.dto.AssignComplaintRequest;
import com.example.demo.dto.ComplaintCreateRequest;
import com.example.demo.dto.ComplaintResponse;
import com.example.demo.dto.SummaryResponse;
import com.example.demo.dto.UpdateComplaintStatusRequest;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.UserAccount;
import com.example.demo.enums.ComplaintStatus;
import com.example.demo.enums.Role;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;

    public ComplaintService(
        ComplaintRepository complaintRepository,
        UserAccountRepository userAccountRepository,
        NotificationService notificationService
    ) {
        this.complaintRepository = complaintRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request) {
        UserAccount user = findUserById(request.getUserId());
        ensureRole(user, Role.USER, "Only users can submit complaints");

        Complaint complaint = new Complaint();
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setSubmittedBy(user);
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        Complaint saved = complaintRepository.save(complaint);
        notificationService.notifyComplaintSubmitted(saved);
        return ComplaintResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsForUser(Long userId) {
        return complaintRepository.findBySubmittedByIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(ComplaintResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll()
            .stream()
            .map(ComplaintResponse::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> getComplaintsForStaff(Long staffId) {
        return complaintRepository.findByAssignedToIdOrderByUpdatedAtDesc(staffId)
            .stream()
            .map(ComplaintResponse::fromEntity)
            .toList();
    }

    @Transactional
    public ComplaintResponse assignComplaint(Long complaintId, AssignComplaintRequest request) {
        UserAccount admin = findUserById(request.getAdminId());
        ensureRole(admin, Role.ADMIN, "Only admin can assign complaints");

        UserAccount staff = findUserById(request.getStaffId());
        ensureRole(staff, Role.STAFF, "Complaint can only be assigned to staff");

        Complaint complaint = findComplaintById(complaintId);
        complaint.setAssignedTo(staff);
        complaint.setAdminRemarks(request.getAdminRemarks());
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        Complaint saved = complaintRepository.save(complaint);
        notificationService.notifyComplaintAssigned(saved);
        return ComplaintResponse.fromEntity(saved);
    }

    @Transactional
    public ComplaintResponse updateComplaintStatus(Long complaintId, UpdateComplaintStatusRequest request) {
        UserAccount staff = findUserById(request.getStaffId());
        ensureRole(staff, Role.STAFF, "Only staff can update complaint status");

        Complaint complaint = findComplaintById(complaintId);
        if (complaint.getAssignedTo() == null || !complaint.getAssignedTo().getId().equals(staff.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Complaint is not assigned to this staff member");
        }

        ComplaintStatus next = request.getStatus();
        if (next == ComplaintStatus.SUBMITTED || next == ComplaintStatus.ASSIGNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff can set status only to IN_PROGRESS or RESOLVED");
        }

        complaint.setStatus(next);
        complaint.setStaffRemarks(request.getStaffRemarks());
        complaint.setResolutionNotes(request.getResolutionNotes());

        Complaint saved = complaintRepository.save(complaint);
        notificationService.notifyStatusUpdated(saved);
        return ComplaintResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public SummaryResponse getSummary() {
        SummaryResponse summary = new SummaryResponse();
        long submitted = complaintRepository.countByStatus(ComplaintStatus.SUBMITTED);
        long assigned = complaintRepository.countByStatus(ComplaintStatus.ASSIGNED);
        summary.setTotal(complaintRepository.count());
        summary.setSubmitted(submitted);
        summary.setAssigned(assigned);
        summary.setPending(submitted + assigned);
        summary.setInProgress(complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS));
        summary.setResolved(complaintRepository.countByStatus(ComplaintStatus.RESOLVED));
        return summary;
    }

    private UserAccount findUserById(Long id) {
        return userAccountRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    private Complaint findComplaintById(Long id) {
        return complaintRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found: " + id));
    }

    private void ensureRole(UserAccount user, Role expectedRole, String message) {
        if (user.getRole() != expectedRole) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
}
