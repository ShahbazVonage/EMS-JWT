package com.auth.dto;

import com.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private int age;
    private BigDecimal salary;
    private boolean isActive;
    private Role role;
    private Long managerId;
}
