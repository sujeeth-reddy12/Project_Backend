package com.example.demo.service;

import com.example.demo.dto.UserResponse;
import com.example.demo.dto.UserUpsertRequest;
import com.example.demo.entity.UserAccount;
import com.example.demo.enums.Role;
import com.example.demo.repository.ComplaintRepository;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserManagementService {

    private final UserAccountRepository userAccountRepository;
    private final ComplaintRepository complaintRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(
        UserAccountRepository userAccountRepository,
        ComplaintRepository complaintRepository,
        NotificationRepository notificationRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.complaintRepository = complaintRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userAccountRepository.findAll()
            .stream()
            .map(UserResponse::fromEntity)
            .toList();
    }

    @Transactional
    public UserResponse createUser(UserUpsertRequest request) {
        if (userAccountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
        }

        UserAccount user = new UserAccount();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() == null ? Role.USER : request.getRole());

        return UserResponse.fromEntity(userAccountRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpsertRequest request) {
        UserAccount user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        userAccountRepository.findByEmail(request.getEmail())
            .filter(existing -> !existing.getId().equals(userId))
            .ifPresent(existing -> {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already registered");
            });

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() == null ? user.getRole() : request.getRole());

        return UserResponse.fromEntity(userAccountRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long userId) {
        UserAccount user = userAccountRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));

        // Unassign complaints where this user was staff
        complaintRepository.unassignComplaintsByUserId(userId);

        // Delete notifications for this user
        notificationRepository.deleteByUserId(userId);

        // Delete complaints submitted by this user
        complaintRepository.deleteBySubmittedById(userId);

        userAccountRepository.delete(user);
    }
}
