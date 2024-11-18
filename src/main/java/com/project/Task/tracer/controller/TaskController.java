package com.project.Task.tracer.controller;

import com.project.Task.tracer.dto.task.TaskRequest;
import com.project.Task.tracer.dto.task.TaskResponse;
import com.project.Task.tracer.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(taskRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID id, @RequestBody TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
