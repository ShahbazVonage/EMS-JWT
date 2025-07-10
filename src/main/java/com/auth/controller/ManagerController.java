package com.auth.controller;

import com.auth.config.CustomerUserDetails;
import com.auth.dto.UserResponseDto;
import com.auth.entity.User;
import com.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/manager") @RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_MANAGER')")
public class ManagerController {
    private final UserService userService;

    @GetMapping("/team/{managerId}")
    public ResponseEntity<List<UserResponseDto>> getTeam(@PathVariable Long managerId){
        List<User> team = userService.getTeamByManager(managerId);
        List<UserResponseDto> teamDto = team.stream()
                .map(userService :: convertToDto).toList();
        return ResponseEntity.ok(teamDto);
    }

    @DeleteMapping("/deactivate/{userId}")
    public ResponseEntity<String> deactivateTeamMember(@PathVariable Long userId , @AuthenticationPrincipal CustomerUserDetails manager) throws AccessDeniedException {
        userService.deactivateUser(userId , manager.getEmail());
        return ResponseEntity.ok("Team Member deactivated Successfully");
    }
}
