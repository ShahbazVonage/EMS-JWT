package com.auth.entity;

import com.auth.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username can not be blank")
    private String username;

    @Column(unique = true , nullable = false)
    @NotBlank(message = "Email is a unique, can not be blank")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @Min(value = 18, message = "Minimum age must be 18 or above")
    private int age;

    @Min(value = 15000 , message = "Minimum salary must be 15000 or above")
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is required")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @Column(nullable = false)
    private boolean isActive = true;

}
