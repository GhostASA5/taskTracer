package com.project.Task.tracer.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Создание комментария")
public class CommentRequest {

    @Schema(description = "Текст комментария")
    @NotNull
    @NotEmpty
    private String commentText;
}
