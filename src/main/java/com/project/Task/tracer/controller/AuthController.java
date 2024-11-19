package com.project.Task.tracer.controller;

import com.project.Task.tracer.dto.auth.AuthenticateRequest;
import com.project.Task.tracer.dto.auth.AuthenticateResponse;
import com.project.Task.tracer.dto.user.UserRequest;
import com.project.Task.tracer.dto.user.UserResponse;
import com.project.Task.tracer.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticateResponse> login(@RequestBody @Valid AuthenticateRequest authenticateRequest,
                                                      HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(authenticateRequest, response));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<AuthenticateResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(request, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerAccount(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok(authService.registerAccount(userRequest));
    }
}
