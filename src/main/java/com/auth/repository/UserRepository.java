package com.auth.repository;

import com.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {
    Optional<User> findByEmail(String email);
    List<User> findByManagerIdAndIsActiveTrue(Long managerId);
    Optional<User> findById(Long managerId);
    Optional<User> findByIdAndIsActiveTrue(Long employeeId);
    Optional<User> findByEmailAndIsActiveTrue(String email);
}
