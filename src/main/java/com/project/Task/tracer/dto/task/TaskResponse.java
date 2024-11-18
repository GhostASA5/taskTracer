package com.project.Task.tracer.dto.task;

import com.project.Task.tracer.model.task.Priority;
import com.project.Task.tracer.model.task.Status;
import lombok.Data;

import java.util.UUID;

@Data
public class TaskResponse {

    private UUID id;

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private String authorId;

    private String executedId;
}
