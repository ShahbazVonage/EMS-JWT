package com.auth.dto;

import com.auth.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class CreateUserDto {
    @NotBlank(message = "Username can not be blank")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password can't not be null")
    private String password;

    @Min(value = 18, message = "Minimum age must be 18 or above")
    private int age;

    @Min(value = 15000 , message = "Minimum salary must be 15000 or above")
    private BigDecimal salary;

    @NotNull(message = "Role is required")
    private Role role;

    private Long managerId;
}
