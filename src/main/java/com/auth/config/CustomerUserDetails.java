package com.auth.config;

import com.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class wraps a User entity and provides user information
 * required by Spring Security for authentication and authorization.
 */
public class CustomerUserDetails implements UserDetails {

    private final User user;

    public CustomerUserDetails(User user){
        this.user  = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
    public String getEmail() {
        return user.getEmail();
    }
}
