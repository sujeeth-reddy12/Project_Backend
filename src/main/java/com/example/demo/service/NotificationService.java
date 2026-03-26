package com.example.demo.service;

import com.example.demo.dto.NotificationResponse;
import com.example.demo.entity.Complaint;
import com.example.demo.entity.NotificationRecord;
import com.example.demo.entity.UserAccount;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final UserAccountRepository userAccountRepository;

    public NotificationService(NotificationRepository notificationRepository, UserAccountRepository userAccountRepository) {
        this.notificationRepository = notificationRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        ensureUserExists(userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(NotificationResponse::fromEntity)
            .toList();
    }

    @Transactional
    public void notifyComplaintSubmitted(Complaint complaint) {
        createNotification(
            complaint.getSubmittedBy(),
            complaint,
            "COMPLAINT_SUBMITTED",
            "Complaint #" + complaint.getId() + " has been submitted successfully."
        );
    }

    @Transactional
    public void notifyComplaintAssigned(Complaint complaint) {
        createNotification(
            complaint.getSubmittedBy(),
            complaint,
            "COMPLAINT_ASSIGNED",
            "Complaint #" + complaint.getId() + " has been assigned to " + complaint.getAssignedTo().getName() + "."
        );
        createNotification(
            complaint.getAssignedTo(),
            complaint,
            "NEW_ASSIGNMENT",
            "You have been assigned complaint #" + complaint.getId() + "."
        );
    }

    @Transactional
    public void notifyStatusUpdated(Complaint complaint) {
        createNotification(
            complaint.getSubmittedBy(),
            complaint,
            "STATUS_UPDATED_" + complaint.getStatus().name(),
            "Complaint #" + complaint.getId() + " status updated to " + complaint.getStatus().name() + "."
        );
    }

    private void createNotification(UserAccount user, Complaint complaint, String event, String message) {
        NotificationRecord notification = new NotificationRecord();
        notification.setUser(user);
        notification.setComplaint(complaint);
        notification.setEvent(event);
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);

        LOGGER.info(
            "Notification [{}] sent to {} for complaint #{}",
            event,
            user.getEmail(),
            complaint.getId()
        );
    }

    private void ensureUserExists(Long userId) {
        if (!userAccountRepository.existsById(userId)) {
            throw new ResponseStatusException(NOT_FOUND, "User not found: " + userId);
        }
    }
}
