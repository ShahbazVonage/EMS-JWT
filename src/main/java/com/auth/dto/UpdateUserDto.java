package com.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class UpdateUserDto {
    private String username;
    private String email;
    private String password;
    private int age;
    private BigDecimal salary;
    private Long managerId;
}
