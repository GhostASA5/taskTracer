package com.project.Task.tracer.services;

import com.project.Task.tracer.dto.user.UpdateUserRequest;
import com.project.Task.tracer.dto.user.UserRequest;
import com.project.Task.tracer.dto.user.UserResponse;
import com.project.Task.tracer.mapper.UserMapper;
import com.project.Task.tracer.model.user.Role;
import com.project.Task.tracer.model.user.RoleType;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.UserRepository;
import com.project.Task.tracer.service.AuthService;
import com.project.Task.tracer.service.UserService;
import com.project.Task.tracer.testContainer.PostgresContainer;
import com.project.Task.tracer.utils.BeanUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Tests for UserService")
public class UserServiceTests extends PostgresContainer {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private User user = User.builder().id(UUID.randomUUID())
            .username("username")
            .email("email")
            .password("password")
            .build();

    @InjectMocks
    private UserService userService;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test getUserByIdResponse")
    public void testGetUserByIdResponse() {
        UUID userId = UUID.randomUUID();


        UserResponse response = UserResponse.builder()
                .id(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.fromUserToResponse(user)).thenReturn(response);

        UserResponse result = userService.getUserByIdResponse(userId);

        assertEquals(response, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).fromUserToResponse(user);
    }

    @Test
    @DisplayName("Test createUser")
    public void testCreateUser() {
        UserRequest request = UserRequest.builder()
                .username("username")
                .email("email")
                .password("password")
                .roleType(RoleType.ADMIN)
                .build();

        Role role = Role.from(RoleType.ADMIN);
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("username")
                .email("email")
                .password("password")
                .roles(Collections.singletonList(role))
                .build();
        role.setUser(user);

        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        when(userMapper.fromRequestToUser(request)).thenReturn(user);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.fromUserToResponse(user)).thenReturn(response);

        UserResponse result = userService.createUser(request);

        assertEquals(response, result);

        verify(userMapper, times(1)).fromRequestToUser(request);
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).fromUserToResponse(user);
    }

    @Test
    @DisplayName("Test updateUser")
    public void testUpdateUser() {
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .username("new username")
                .email("new email")
                .build();

        Role role = Role.from(RoleType.ADMIN);
        User user = User.builder()
                .id(userId)
                .username("username")
                .email("email")
                .password("password")
                .roles(Collections.singletonList(role))
                .build();
        role.setUser(user);

        User updateUser = User.builder()
                .id(userId)
                .username("new username")
                .email("new email")
                .password("password")
                .roles(Collections.singletonList(role))
                .build();

        UserResponse response = UserResponse.builder()
                .id(userId)
                .username("new username")
                .email("new email")
                .build();

        mockStatic(AuthService.class);
        when(AuthService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.fromRequestToUser(request)).thenReturn(updateUser);
        when(userRepository.existsByEmail(updateUser.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(updateUser.getUsername())).thenReturn(false);

        BeanUtils.copyNonNullProperties(updateUser, user);

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.fromUserToResponse(user)).thenReturn(response);

        UserResponse result = userService.updateUser(userId, request);

        assertEquals(response, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).fromUserToResponse(user);
        verify(userRepository, times(1)).existsByEmail(updateUser.getEmail());
        verify(userRepository, times(1)).existsByUsername(updateUser.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).fromUserToResponse(user);
    }

    @Test
    @DisplayName("Test deleteUser")
    public void testDeleteUser() {
        UUID userId = UUID.randomUUID();

        Role role = Role.from(RoleType.ADMIN);
        User user = User.builder()
                .id(userId)
                .username("username")
                .email("email")
                .password("password")
                .roles(Collections.singletonList(role))
                .build();
        role.setUser(user);

        mockStatic(AuthService.class);
        when(AuthService.getCurrentUserId()).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        verify(userRepository, times(1)).findById(userId);
    }
}
