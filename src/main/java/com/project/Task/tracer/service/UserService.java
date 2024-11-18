package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.user.UserRequest;
import com.project.Task.tracer.dto.user.UserResponse;
import com.project.Task.tracer.exception.UserAlreadyExistException;
import com.project.Task.tracer.exception.UserNotFoundException;
import com.project.Task.tracer.mapper.UserMapper;
import com.project.Task.tracer.model.user.Role;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.UserRepository;
import com.project.Task.tracer.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(MessageFormat.format("User with {0} not found", id))
        );
        return userMapper.fromUserToResponse(user);
    }

    public UserResponse createUser(UserRequest request) {
        User newUser = userMapper.fromRequestToUser(request);
        checkUserData(newUser);
        Role role = Role.from(request.getRoleType());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRoles(Collections.singletonList(role));
        role.setUser(newUser);
        userRepository.save(newUser);

        return userMapper.fromUserToResponse(newUser);
    }

    public UserResponse updateUser(UUID id, UserRequest request) {
        User updateUser = userMapper.fromRequestToUser(request);
        Optional<User> excitedUser = userRepository.findById(id);

        if (excitedUser.isPresent()) {
            checkUserData(updateUser);
            BeanUtils.copyNonNullProperties(updateUser, excitedUser.get());
            return userMapper.fromUserToResponse(userRepository.save(excitedUser.get()));
        }
        throw new UserNotFoundException(MessageFormat.format("User with {0} not found", id));
    }

    public void deleteUser(UUID id) {
        Optional<User> excitedUser = userRepository.findById(id);
        if (excitedUser.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException(MessageFormat.format("User with {0} not found", id));
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UserNotFoundException(MessageFormat.format("User with email {0} not found.", email))
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

}
