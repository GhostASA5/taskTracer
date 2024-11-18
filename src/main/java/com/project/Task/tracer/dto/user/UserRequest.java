package com.project.Task.tracer.dto.user;

import com.project.Task.tracer.model.user.RoleType;
import lombok.Data;

@Data
public class UserRequest {

    private String username;

    private String email;

    private String password;

    private RoleType roleType;
}
