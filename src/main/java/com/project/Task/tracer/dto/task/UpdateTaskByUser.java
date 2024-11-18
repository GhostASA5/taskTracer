package com.project.Task.tracer.dto.task;

import com.project.Task.tracer.model.task.Status;
import lombok.Data;

@Data
public class UpdateTaskByUser {

    private Status status;

    private String comment;
}
