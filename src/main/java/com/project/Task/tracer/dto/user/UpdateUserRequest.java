package com.project.Task.tracer.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Обновление пользователя")
public class UpdateUserRequest {

    @Schema(description = "Обновление имени пользователя")
    private String username;

    @Schema(description = "Обновление почты пользователя")
    private String email;
}
