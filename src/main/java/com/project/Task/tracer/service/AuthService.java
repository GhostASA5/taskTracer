package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.auth.AuthenticateRequest;
import com.project.Task.tracer.dto.auth.AuthenticateResponse;
import com.project.Task.tracer.dto.user.UserRequest;
import com.project.Task.tracer.dto.user.UserResponse;
import com.project.Task.tracer.exception.RefreshTokenException;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.security.AppUserPrincipal;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthenticateResponse login(AuthenticateRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        var accessToken = jwtService.generateAccessToken((AppUserPrincipal) userDetails);
        var refreshToken = jwtService.generateRefreshToken((AppUserPrincipal) userDetails);

        Cookie cookie = new Cookie("Refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
        response.addHeader("Authorization", "Bearer " + accessToken);
        return new AuthenticateResponse(accessToken, refreshToken);
    }

    public AuthenticateResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Refresh_token")) {
                String refreshToken = cookie.getValue();
                if (jwtService.validateToken(refreshToken)) {
                    UUID uuid = UUID.fromString(jwtService.getUUID(refreshToken));
                    User user = userService.getUserById(uuid);
                    var accessToken = jwtService.generateAccessToken(user.getId(), user.getRoles());

                    Cookie newCookie = new Cookie("Refresh_token", refreshToken);
                    newCookie.setHttpOnly(true);
                    newCookie.setSecure(true);
                    response.addCookie(newCookie);
                    response.addHeader("Authorization", "Bearer " + accessToken);
                    return new AuthenticateResponse(accessToken, refreshToken);
                }
                throw new RefreshTokenException("Refresh token is not valid or expired");
            }
        }
        throw new RefreshTokenException("Did not find refresh token");
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

    public static UUID getCurrentUserId() {
        return UUID.fromString(
                (String) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
    }
}
