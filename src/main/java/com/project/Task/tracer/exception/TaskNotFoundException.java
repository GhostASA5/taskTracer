package com.project.Task.tracer.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String message) {
        super(message);
    }
}
