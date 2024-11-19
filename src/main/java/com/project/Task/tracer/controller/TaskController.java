package com.project.Task.tracer.controller;

import com.project.Task.tracer.dto.task.TaskFilterRequest;
import com.project.Task.tracer.dto.task.TaskListResponse;
import com.project.Task.tracer.dto.task.TaskRequest;
import com.project.Task.tracer.dto.task.TaskResponse;
import com.project.Task.tracer.model.task.Status;
import com.project.Task.tracer.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/author/{authorId}")
    public ResponseEntity<TaskListResponse> getTasksByAuthorId(@PathVariable("authorId") UUID authorId,
                                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                                               @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getTasksByAuthorId(authorId, page, size));
    }

    @GetMapping("/executor/{executorId}")
    public ResponseEntity<TaskListResponse> getTasksByExecutorId(@PathVariable("executorId") UUID executorId,
                                                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getTasksByExecutorId(executorId, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<TaskListResponse> getTasksByFilter(@RequestParam(name = "page", defaultValue = "0") int page,
                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                             @RequestBody @Valid TaskFilterRequest filterRequest) {
        return ResponseEntity.ok(taskService.getTasksWithFilter(filterRequest, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(taskService.getTask(id));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.createTask(taskRequest));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable("id") UUID id, @RequestBody @Valid TaskRequest taskRequest) {
        return ResponseEntity.ok(taskService.updateTask(id, taskRequest));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable("id") UUID id, @RequestParam Status status) {
        taskService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponse> deleteTask(@PathVariable("id") UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
}
