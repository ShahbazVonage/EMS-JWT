package com.auth.controller;

import com.auth.entity.User;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/profile")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_EMPLOYEE' , 'ROLE_MANAGER')")
public class EmployeeController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<User> getOwnProfile(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.getProfileByEmail(user.getEmail()));
    }
}
