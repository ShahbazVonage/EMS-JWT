package com.auth.controller;

import com.auth.entity.User;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/manager") @RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ManagerController {
    private final UserService userService;

    @GetMapping("/team/{managerId}")
    public ResponseEntity<List<User>> getTeam(@PathVariable Long managerId){
        List<User> team = userService.getTeamByManager(managerId);
        return ResponseEntity.ok(team);
    }
}
