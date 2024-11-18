package com.project.Task.tracer.dto.auth;

import lombok.Data;

@Data
public class AuthenticateRequest {

    private String email;

    private String password;
}
