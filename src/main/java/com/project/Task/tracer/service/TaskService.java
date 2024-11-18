package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.task.*;
import com.project.Task.tracer.exception.TaskNotFoundException;
import com.project.Task.tracer.mapper.TaskMapper;
import com.project.Task.tracer.model.task.Task;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.TaskRepository;
import com.project.Task.tracer.repository.TaskSpecification;
import com.project.Task.tracer.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final UserService userService;

    public TaskListResponse getTasksByAuthorId(UUID authorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskMapper.fromListToTaskListResponse(taskRepository.findAllByAuthor_Id(authorId, pageable));
    }

    public TaskListResponse getTasksByExecutorId(UUID executorId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return taskMapper.fromListToTaskListResponse(taskRepository.findAllByExecutor_Id(executorId, pageable));
    }

    public TaskListResponse getTasksWithFilter(TaskFilterRequest filterRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Task> tasks = taskRepository.findAll(
                TaskSpecification.findWithFilter(filterRequest), pageable
        ).getContent();
        return taskMapper.fromListToTaskListResponse(tasks);
    }

    public TaskResponse getTask(UUID taskId) {
        return taskMapper.fromTaskToResponse(getTaskById(taskId));
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
        Task newTask = taskMapper.fromRequestToTask(taskRequest);
        User author = userService.getUserById(taskRequest.getAuthorId());
        User executor = userService.getUserById(taskRequest.getExecutedId());
        newTask.setAuthor(author);
        newTask.setExecutor(executor);
        return taskMapper.fromTaskToResponse(taskRepository.save(newTask));
    }

    public TaskResponse updateTask(UUID taskId, TaskRequest taskRequest) {
        Task updatedTask = taskMapper.fromRequestToTask(taskRequest);
        Task existedTask = getTaskById(taskId);

        BeanUtils.copyNonNullProperties(updatedTask, existedTask);
        if (taskRequest.getAuthorId() != null) {
            User author = userService.getUserById(taskRequest.getAuthorId());
            existedTask.setAuthor(author);
        }
        if (taskRequest.getExecutedId() != null) {
            User executor = userService.getUserById(taskRequest.getExecutedId());
            existedTask.setExecutor(executor);
        }
        return taskMapper.fromTaskToResponse(taskRepository.save(existedTask));
    }

    public TaskResponse updateTaskByUser(UUID taskId, UpdateTaskByUser taskRequest) {
        Task updatedTask = getTaskById(taskId);

        if (taskRequest.getStatus() != null) {
            updatedTask.setStatus(taskRequest.getStatus());
        }
        if (taskRequest.getComment() != null) {
            String existedComment = updatedTask.getComment();
            String addComment = existedComment != null ? existedComment
                    .concat(taskRequest.getComment()) : taskRequest.getComment();
            updatedTask.setComment(addComment);
        }
        return taskMapper.fromTaskToResponse(taskRepository.save(updatedTask));
    }

    public void deleteTask(UUID taskId) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
        }
        throw new TaskNotFoundException(MessageFormat.format("Task with id {0} not found", taskId));
    }

    private Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException(MessageFormat.format("Task with id {0} not found", taskId))
        );
    }
}
