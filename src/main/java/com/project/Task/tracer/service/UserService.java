package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.user.UpdateUserRequest;
import com.project.Task.tracer.dto.user.UserRequest;
import com.project.Task.tracer.dto.user.UserResponse;
import com.project.Task.tracer.exception.ForbiddenException;
import com.project.Task.tracer.exception.UserAlreadyExistException;
import com.project.Task.tracer.exception.UserNotFoundException;
import com.project.Task.tracer.mapper.UserMapper;
import com.project.Task.tracer.model.user.Role;
import com.project.Task.tracer.model.user.RoleType;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.UserRepository;
import com.project.Task.tracer.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserByIdResponse(UUID id) {
        log.info("UserService: call getUserByIdResponse: {}", id);
        return userMapper.fromUserToResponse(getUserById(id));
    }

    public UserResponse createUser(UserRequest request) {
        log.info("UserService: call createUser");
        User newUser = userMapper.fromRequestToUser(request);
        checkUserData(newUser);
        Role role = Role.from(request.getRoleType());
        newUser.setRoles(Collections.singletonList(role));
        role.setUser(newUser);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(newUser);

        return userMapper.fromUserToResponse(newUser);
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        log.info("UserService: call updateUser: {}", id);
        User loggedUser = getUserById(AuthService.getCurrentUserId());
        checkUserAuthority(loggedUser, id);

        User updateUser = userMapper.fromRequestToUser(request);
        checkUserData(updateUser);
        BeanUtils.copyNonNullProperties(updateUser, loggedUser);
        return userMapper.fromUserToResponse(userRepository.save(loggedUser));
    }

    public void deleteUser(UUID id) {
        log.info("UserService: call deleteUser: {}", id);
        User loggedUser = getUserById(AuthService.getCurrentUserId());
        checkUserAuthority(loggedUser, id);

        User user = getUserById(id);
        userRepository.delete(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(MessageFormat.format("User with email {0} not found.", email))
        );
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(MessageFormat.format("User with {0} not found", id))
        );
    }

    private void checkUserData(User newUser){
        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new UserAlreadyExistException(
                    MessageFormat.format("User with username {0} already exist.", newUser.getUsername()));
        } else if (userRepository.existsByEmail(newUser.getEmail())) {
            throw new UserAlreadyExistException(
                    MessageFormat.format("User with email {0} already exist.", newUser.getEmail()));
        }
    }

    private void checkUserAuthority(User user, UUID targetId) {
        if (!user.getRoles().get(0).getRole().equals(RoleType.ADMIN) && !user.getId().equals(targetId)) {
            throw new ForbiddenException(MessageFormat.format(
                    "The account {0} is not yours. You do not have the rights to change this account.",
                    targetId
            ));
        }
    }

}
