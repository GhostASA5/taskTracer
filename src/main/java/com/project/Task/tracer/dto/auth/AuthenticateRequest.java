package com.project.Task.tracer.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификация.")
public class AuthenticateRequest {

    @Schema(description = "Почта пользователя")
    @NotNull
    @NotEmpty
    private String email;

    @Schema(description = "Пароль пользователя")
    @NotNull
    @NotEmpty
    private String password;
}
