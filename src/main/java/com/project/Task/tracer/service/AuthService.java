package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.auth.AuthenticateRequest;
import com.project.Task.tracer.dto.auth.AuthenticateResponse;
import com.project.Task.tracer.dto.user.UserRequest;
import com.project.Task.tracer.dto.user.UserResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    public AuthenticateResponse login(AuthenticateRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

//        var jwt = jwtService.generateToken(user);
//        var refreshToken = refreshTokenService.generateRefreshTokenByUserId(user.getId());

        Cookie cookie = new Cookie("Refresh_token", "2");
        Cookie cookie2 = new Cookie("Access_token", "2");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        response.addCookie(cookie2);
        response.addHeader("Authorization", "Bearer " + "jwt");
        return new AuthenticateResponse("2", "2");
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Refresh_token", "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public UserResponse registerAccount(UserRequest request) {
        return userService.createUser(request);
    }

}