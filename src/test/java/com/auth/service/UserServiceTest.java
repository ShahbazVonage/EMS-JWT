package com.auth.service;

import com.auth.dto.CreateUserDto;
import com.auth.dto.UpdateUserDto;
import com.auth.entity.User;
import com.auth.enums.Role;
import com.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void testCreateUser(){
        CreateUserDto requestDto = new CreateUserDto();
        requestDto.setUsername("Shahbaz");
        requestDto.setEmail("Shahbaz123@gmail.com");
        requestDto.setPassword("Shahbaz123");
        requestDto.setAge(21);
        requestDto.setSalary(BigDecimal.valueOf(50000));
        requestDto.setRole(Role.EMPLOYEE);

        String encodedPass = "ENCODED_PASS_HAI_BHAI_MANA_KR";

        when(passwordEncoder.encode("Shahbaz123")).thenReturn(encodedPass);

        User savedUser = new User();
        savedUser.setUsername("Shahbaz");
        savedUser.setEmail("Shahbaz123@gmail.com");
        savedUser.setPassword(encodedPass);
        savedUser.setAge(21);
        savedUser.setSalary(BigDecimal.valueOf(50000));
        savedUser.setRole(Role.EMPLOYEE);

        when(repository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        User result = service.createUser(requestDto);

        assertEquals(savedUser.getPassword() , result.getPassword());
        assertEquals(savedUser.getUsername() , result.getUsername());
    }

    @Test
    void testCreateUser_AdminRoleShouldThrowException(){
        CreateUserDto requestDto = new CreateUserDto();
        requestDto.setUsername("Shahbaz");
        requestDto.setEmail("Shahbaz123@gmail.com");
        requestDto.setPassword("Shahbaz123");
        requestDto.setAge(21);
        requestDto.setSalary(BigDecimal.valueOf(50000));
        requestDto.setRole(Role.ADMIN);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.createUser(requestDto));
        assertEquals("Cannot create ADMIN users via API." , ex.getMessage());


    }

    @Test
    void testCreateUser_ManagerNotFoundException(){

        CreateUserDto request = new CreateUserDto();
        request.setEmail("Shahbaz123@gmail.com");
        request.setRole(Role.EMPLOYEE);
        request.setManagerId(1L);

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> service.createUser(request));
        assertEquals("Manager not found" , ex.getMessage());

    }

    @Test
    void testPromoteToManager(){
        Long userId = 2L;

        User user = new User();
        user.setId(userId);
        user.setUsername("Shahbaz Khan");
        user.setEmail("Shahbaz123@gmail.com");
        user.setAge(21);
        user.setPassword("Encode password");
        user.setRole(Role.EMPLOYEE);

        when(repository.findById(userId)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        service.promoteToManager(userId);

        assertEquals(Role.MANAGER , user.getRole());
    }

    @Test
    void testPromoteToManager_ManagerNotFoundException(){

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class ,
                () -> service.promoteToManager(2L));

        assertEquals("User not found" , ex.getMessage());
    }

    @Test
    void testGetTeamByManager(){
        Long manId = 1L;
        User manager = new User();
        manager.setId(manId);
        manager.setRole(Role.MANAGER);
        manager.setUsername("Shwetha");

        User user1 = new User();
        user1.setUsername("Shahbaz");
        user1.setManager(manager);
        user1.setRole(Role.EMPLOYEE);
        User user2 = new User();
        user2.setUsername("Mithlesh");
        user2.setManager(manager);
        user2.setRole(Role.EMPLOYEE);
        User user3 = new User();
        user3.setUsername("Shahbaz");
        user3.setManager(manager);
        user3.setRole(Role.EMPLOYEE);

        List<User> employees = List.of(user1 , user2 , user3);

        when(repository.findByManagerIdAndIsActiveTrue(manId)).thenReturn(employees);

        List<User> result = service.getTeamByManager(manId);

        assertEquals(result , employees);


    }

    @Test
    void testGetProfileByEmail(){
        String email = "Shahbaz123@gmail.com";
        User user = new User();
        user.setUsername("Shahbaz");
        user.setEmail(email);
        user.setRole(Role.EMPLOYEE);

        when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = service.getProfileByEmail(email);

        assertEquals(user.getEmail() , result.getEmail());
        assertEquals(user.getUsername() , result.getUsername());
    }

    @Test
    void testUpdateProfile(){
        String email = "Shahbaz123@gmail.com";
        User user = new User();
        user.setEmail(email);
        user.setUsername("Shahbaz");
        user.setRole(Role.EMPLOYEE);

        UpdateUserDto request = new UpdateUserDto();
        request.setUsername("Shahbaz Khan");

        when(repository.findByEmailAndIsActiveTrue(email)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        User result = service.updateProfile(email , request);

        assertEquals(request.getUsername() , result.getUsername());
    }

    @Test
    void  testDeactivateUser() throws AccessDeniedException {
        Long userId = 2L;
        String managerEmail = "Shwetha123@gmail.com";

        User manager = new User();
        manager.setId(4L);
        manager.setUsername("Shwetha");
        manager.setEmail(managerEmail);
        manager.setRole(Role.MANAGER);

        User user = new User();
        user.setId(userId);
        user.setUsername("Shahbaz");
        user.setEmail("Shahbaz123@gmail.com");
        user.setRole(Role.EMPLOYEE);
        user.setManager(manager);

        when(repository.findByEmail(managerEmail)).thenReturn(Optional.of(manager));
        when(repository.findByIdAndIsActiveTrue(userId)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        service.deactivateUser(userId , managerEmail);

        assertFalse(user.isActive());
    }


}
