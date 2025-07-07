package com.auth.service;

import com.auth.dto.CreateUserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(CreateUserDto createUserDto){
        User user = new User();
        try{
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
            if(createUserDto.getManagerId() !=null ){
                User manager = repository.findById(createUserDto.getManagerId()).orElseThrow(() -> new UsernameNotFoundException("Manager not found"));
                user.setManager(manager);
            }
            return repository.save(user);
        } catch (Exception ex){
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }
    public void promoteToManager(Long userId){
        User user = repository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try{
            user.setRole(Role.MANAGER);
            repository.save(user);
        }catch (Exception ex){
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }
    public List<User> getTeamByManager(Long managerId){
        try {
            return repository.findByManagerId(managerId);
        } catch (Exception ex){
            throw new RuntimeException("Error: " + ex.getMessage());
        }
    }
    public User getProfileByEmail(String email){
            User user = repository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return user;
    }
}
