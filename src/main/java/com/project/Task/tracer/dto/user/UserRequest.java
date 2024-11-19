package com.project.Task.tracer.dto.user;

import com.project.Task.tracer.model.user.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Создание пользователя")
public class UserRequest {

    @Schema(description = "Обновление имени пользователя")
    @NotNull
    @NotEmpty
    private String username;

    @Schema(description = "Обновление почты пользователя")
    @NotNull
    @NotEmpty
    private String email;

    @Schema(description = "Пароль пользователя")
    @NotNull
    @NotEmpty
    private String password;

    @Schema(description = "Роль полтзователя")
    @NotNull
    @NotEmpty
    private RoleType roleType;
}
