package com.project.Task.tracer.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticateResponse {

    private String accessToken;

    private String refreshToken;
}
