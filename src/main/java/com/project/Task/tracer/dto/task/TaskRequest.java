package com.project.Task.tracer.dto.task;

import com.project.Task.tracer.model.task.Priority;
import com.project.Task.tracer.model.task.Status;
import lombok.Data;

@Data
public class TaskRequest {

    private String title;

    private String description;

    private Status status;

    private Priority priority;

    private String authorId;

    private String executedId;

}