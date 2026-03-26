package com.example.demo.repository;

import com.example.demo.entity.Complaint;
import com.example.demo.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findBySubmittedByIdOrderByCreatedAtDesc(Long submittedById);

    List<Complaint> findByAssignedToIdOrderByUpdatedAtDesc(Long assignedToId);

    long countByStatus(ComplaintStatus status);

    boolean existsBySubmittedById(Long submittedById);

    boolean existsByAssignedToId(Long assignedToId);

    void deleteBySubmittedById(Long submittedById);

    @Modifying
    @Query("UPDATE Complaint c SET c.assignedTo = null WHERE c.assignedTo.id = :userId")
    void unassignComplaintsByUserId(@Param("userId") Long userId);
}
