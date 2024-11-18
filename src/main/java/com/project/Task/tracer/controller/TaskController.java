package com.project.Task.tracer.controller;

import com.project.Task.tracer.dto.task.TaskFilterRequest;
import com.project.Task.tracer.dto.task.TaskListResponse;
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

    @GetMapping("/author/{authorId}")
    public ResponseEntity<TaskListResponse> getTasksByAuthorId(@PathVariable("authorId") UUID authorId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getTasksByAuthorId(authorId, page, size));
    }

    @GetMapping("/executor/{executorId}")
    public ResponseEntity<TaskListResponse> getTasksByExecutorId(@PathVariable("executorId") UUID executorId,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getTasksByExecutorId(executorId, page, size));
    }

    @GetMapping("/filter")
    public ResponseEntity<TaskListResponse> getTasksByFilter(@RequestBody TaskFilterRequest filterRequest,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(taskService.getTasksWithFilter(filterRequest, page, size));
    }

    @GetMapping("/comments/{taskId}")
    public ResponseEntity findCommentsByTaskId(@PathVariable("taskId") UUID taskId) {
        return null;
    }

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
