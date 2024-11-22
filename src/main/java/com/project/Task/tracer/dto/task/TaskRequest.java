package com.project.Task.tracer.dto.task;

import com.project.Task.tracer.model.task.Priority;
import com.project.Task.tracer.model.task.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Создание задачи")
public class TaskRequest {

    @Schema(description = "Заголовок задачи")
    @NotNull
    @NotEmpty
    private String title;

    @Schema(description = "Описание задачи")
    @NotNull
    @NotEmpty
    private String description;

    @Schema(description = "Статус задачи")
    @NotNull
    @NotEmpty
    private Status status;

    @Schema(description = "Приоритет задачи")
    @NotNull
    @NotEmpty
    private Priority priority;

    @Schema(description = "Id автора задачи")
    @NotNull
    @NotEmpty
    private UUID authorId;

    @Schema(description = "Id исполнителя задачи")
    @NotNull
    @NotEmpty
    private UUID executedId;

}
