package com.auth.controller;

import com.auth.config.CustomUserDetailsService;
import com.auth.config.JwtUtil;
import com.auth.dto.CreateUserDto;
import com.auth.dto.JwtResponse;
import com.auth.dto.LoginRequest;
import com.auth.entity.User;
import com.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> createEmployee(@Valid @RequestBody CreateUserDto request){
        User user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail() , request.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        String role = userDetails.getAuthorities().stream().findFirst().get().getAuthority();
        String token = jwtUtil.generateToken(userDetails.getUsername() , role);
        return ResponseEntity.ok(new JwtResponse(token));
    }
    @PutMapping("/promote/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> promoteToManager(@PathVariable Long userId){
        userService.promoteToManager(userId);
        return ResponseEntity.ok("User Promoted to Manager role");
    }
}
