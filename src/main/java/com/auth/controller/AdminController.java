package com.auth.controller;

import com.auth.config.CustomUserDetailsService;
import com.auth.config.JwtUtil;
import com.auth.dto.CreateUserDto;
import com.auth.dto.JwtResponse;
import com.auth.dto.LoginRequest;
import com.auth.dto.UserResponseDto;
import com.auth.entity.User;
import com.auth.exception.RoleNotFoundException;
import com.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin") @RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Post mapping for creating/Adding Employee to DB
     * @param request body of the employee
     * @return user object of the newly created employee
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> createEmployee(@Valid @RequestBody CreateUserDto request){
        User user = userService.createUser(request);
        return ResponseEntity.ok(userService.convertToDto(user));
    }

    /**
     * Post Mapping for login the employee and get the jwt token
     * @param request username and password credentials
     * @return jwt token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException ex){
            throw new BadCredentialsException("Invalid email or password.");
        }catch (DisabledException ex){
           throw new DisabledException("Your account is deactivated. Contact admin.");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst()
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

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
