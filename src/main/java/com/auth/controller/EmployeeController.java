package com.auth.controller;

import com.auth.config.CustomerUserDetails;
import com.auth.dto.UpdateUserDto;
import com.auth.dto.UserResponseDto;
import com.auth.entity.User;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/profile")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE' , 'ROLE_MANAGER')")
public class EmployeeController {

    private final UserService service;

    @GetMapping
    public ResponseEntity<UserResponseDto> getOwnProfile(@AuthenticationPrincipal CustomerUserDetails user){
        User userData = service.getProfileByEmail(user.getEmail());
        return ResponseEntity.ok(service.convertToDto(userData));
    }

    @PutMapping
    public ResponseEntity<UserResponseDto> updateProfile(@AuthenticationPrincipal CustomerUserDetails user , @RequestBody UpdateUserDto updateRequest){
        User updatedUser = service.updateProfile(user.getEmail() , updateRequest);
        return ResponseEntity.ok(service.convertToDto(updatedUser));
    }
}
