package com.auth.service;

import com.auth.dto.CreateUserDto;
import com.auth.dto.UpdateUserDto;
import com.auth.dto.UserResponseDto;
import com.auth.enums.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Method to create/add user to DB
     *
     * @param createUserDto request data dto
     * @return added employee object
     */
    public User createUser(CreateUserDto createUserDto) {
        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setEmail(createUserDto.getEmail());
        log.info("Encoding password");
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        log.info("Password Encode completed");
        user.setAge(createUserDto.getAge());
        user.setSalary(createUserDto.getSalary());
        // Prevent creating users with ADMIN role
        if (createUserDto.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot create ADMIN users via API.");
        }
        user.setRole(createUserDto.getRole());
        if (createUserDto.getManagerId() != null) {
            User manager = repository.findById(createUserDto.getManagerId()).orElseThrow(() -> new UsernameNotFoundException("Manager not found"));
            user.setManager(manager);
        }
        return repository.save(user);
    }

    /**
     * Method promote Employee to Manager role.
     *
     * @param userId of the employee you want to promote
     */
    public void promoteToManager(Long userId) {
        User user = repository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setRole(Role.MANAGER);
        repository.save(user);

    }

    /**
     * Method returns list of users under the manager
     *
     * @param managerId of manager under whom you want the list of employee
     * @return list of users
     */
    public List<User> getTeamByManager(Long managerId) {
        try {
            return repository.findByManagerIdAndIsActiveTrue(managerId);
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    /**
     * Method to return User object from emailId
     *
     * @param email of the user
     * @return User object
     */
    public User getProfileByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Method to update data of the users
     *
     * @param email         of the user
     * @param updateRequest updated request data
     * @return updated user object
     */
    public User updateProfile(String email, UpdateUserDto updateRequest) {
        User user = repository.findByEmailAndIsActiveTrue(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (updateRequest.getUsername() != null) user.setUsername(updateRequest.getUsername());
        if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
        if (updateRequest.getAge() != 0) user.setAge(updateRequest.getAge());
        if (updateRequest.getSalary() != null) user.setSalary(updateRequest.getSalary());

        if (updateRequest.getPassword() != null && !updateRequest.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }
        if (updateRequest.getManagerId() != null) {
            User manager = repository.findById(updateRequest.getManagerId()).orElseThrow(() -> new UsernameNotFoundException("Manager not found with this Id: " + updateRequest.getManagerId()));
            user.setManager(manager);
        }
        try {
            return repository.save(user);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("Database Error: " + ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }

    /**
     * This method helps to softly delete the employee we set the
     * employee isActive status to false
     *
     * @param userId       of employee whom you want to deactivate
     * @param managerEmail of manager
     * @throws AccessDeniedException exception as ADMIN can not be de-activate and only manager deactivate their team member
     */
    public void deactivateUser(Long userId, String managerEmail) throws AccessDeniedException {
        User manager = repository.findByEmail(managerEmail).orElseThrow(() -> new UsernameNotFoundException("Manager not Found"));
        User targetUser = repository.findByIdAndIsActiveTrue(userId).orElseThrow(() -> new UsernameNotFoundException("User not found or already de-activate"));

        // Admin can not be blocked by manager
        if (targetUser.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("ADMIN can not be deactivate");
        }

        // Manager only able to deactivate their team
        if (targetUser.getManager() == null || !targetUser.getManager().getId().equals(manager.getId())) {
            throw new AccessDeniedException("You can only deactivate your own team members");
        }

        targetUser.setActive(false);
        repository.save(targetUser);
    }

    /**
     * Helper method that convert User entity object to DTO
     *
     * @param user entity
     * @return dto
     */
    public UserResponseDto convertToDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getSalary(),
                user.isActive(),
                user.getRole(),
                user.getManager() == null ? null : user.getManager().getId()
        );
    }
}
