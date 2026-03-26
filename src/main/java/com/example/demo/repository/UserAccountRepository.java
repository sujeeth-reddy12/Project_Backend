package com.example.demo.repository;

import com.example.demo.entity.UserAccount;
import com.example.demo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);

    List<UserAccount> findByRole(Role role);
}
