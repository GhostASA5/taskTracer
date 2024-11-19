package com.project.Task.tracer.dto.task;

import com.project.Task.tracer.model.task.Priority;
import com.project.Task.tracer.model.task.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Фильтрация задач")
public class TaskFilterRequest {

    @Schema(description = "Заголовок задачи")
    private String title;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Статус задачи")
    private Status status;

    @Schema(description = "Приоритет задачи")
    private Priority priority;
}
