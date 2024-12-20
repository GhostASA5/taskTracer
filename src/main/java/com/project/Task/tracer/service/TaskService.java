package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.task.*;
import com.project.Task.tracer.exception.ForbiddenException;
import com.project.Task.tracer.exception.TaskNotFoundException;
import com.project.Task.tracer.mapper.TaskMapper;
import com.project.Task.tracer.model.task.Status;
import com.project.Task.tracer.model.task.Task;
import com.project.Task.tracer.model.user.RoleType;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.TaskRepository;
import com.project.Task.tracer.repository.TaskSpecification;
import com.project.Task.tracer.utils.BeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final UserService userService;

    public TaskListResponse getTasksByAuthorId(UUID authorId, int page, int size) {
        log.info("TaskService: call getTasksByAuthorId: authorId - {}, page - {}, size - {}", authorId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return taskMapper.fromListToTaskListResponse(taskRepository.findAllByAuthor_Id(authorId, pageable));
    }

    public TaskListResponse getTasksByExecutorId(UUID executorId, int page, int size) {
        log.info("TaskService: call getTasksByExecutorId: executorId - {}, page - {}, size - {}", executorId, page, size);
        Pageable pageable = PageRequest.of(page, size);
        return taskMapper.fromListToTaskListResponse(taskRepository.findAllByExecutor_Id(executorId, pageable));
    }

    public TaskListResponse getTasksWithFilter(TaskFilterRequest filterRequest, int page, int size) {
        log.info("TaskService: call getTasksWithFilter: filterRequest - {}, page - {}, size - {}", filterRequest, page, size);
        Pageable pageable = PageRequest.of(page, size);
        List<Task> tasks = taskRepository.findAll(
                TaskSpecification.findWithFilter(filterRequest), pageable
        ).getContent();
        return taskMapper.fromListToTaskListResponse(tasks);
    }

    public TaskResponse getTask(UUID taskId) {
        log.info("TaskService: call getTask: taskId - {}", taskId);
        return taskMapper.fromTaskToResponse(getTaskById(taskId));
    }

    public TaskResponse createTask(TaskRequest taskRequest) {
        log.info("TaskService: call createTask: taskRequest - {}", taskRequest);
        Task newTask = taskMapper.fromRequestToTask(taskRequest);
        User author = userService.getUserById(taskRequest.getAuthorId());
        User executor = userService.getUserById(taskRequest.getExecutedId());
        newTask.setAuthor(author);
        newTask.setExecutor(executor);
        return taskMapper.fromTaskToResponse(taskRepository.save(newTask));
    }

    public TaskResponse updateTask(UUID taskId, TaskRequest taskRequest) {
        log.info("TaskService: call updateTask: taskId - {}", taskId);
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

    public void updateStatus(UUID taskId, Status status) {
        log.info("TaskService: call updateStatus: taskId - {}, status - {}", taskId, status);
        Task updatedTask = getTaskById(taskId);

        UUID userId = AuthService.getCurrentUserId();
        User user = userService.getUserById(userId);

        if (!user.getRoles().get(0).getRole().equals(RoleType.ADMIN) && !updatedTask.getExecutor().getId().equals(userId)) {
            throw new ForbiddenException(MessageFormat.format(
                    "You don`t have the right to change a task status. You are not the executor of the task {0}.", updatedTask.getId()
            ));
        }

        updatedTask.setStatus(status);
        taskRepository.save(updatedTask);
    }

    public void deleteTask(UUID taskId) {
        log.info("TaskService: call deleteTask: taskId - {}", taskId);
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId);
        }
        throw new TaskNotFoundException(MessageFormat.format("Task with id {0} not found", taskId));
    }

    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(
                () -> new TaskNotFoundException(MessageFormat.format("Task with id {0} not found", taskId))
        );
    }
}
