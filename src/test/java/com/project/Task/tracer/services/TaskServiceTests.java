package com.project.Task.tracer.services;

import com.project.Task.tracer.dto.task.TaskListResponse;
import com.project.Task.tracer.dto.task.TaskRequest;
import com.project.Task.tracer.dto.task.TaskResponse;
import com.project.Task.tracer.mapper.TaskMapper;
import com.project.Task.tracer.model.task.Status;
import com.project.Task.tracer.model.task.Task;
import com.project.Task.tracer.model.user.Role;
import com.project.Task.tracer.model.user.RoleType;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.TaskRepository;
import com.project.Task.tracer.service.AuthService;
import com.project.Task.tracer.service.TaskService;
import com.project.Task.tracer.service.UserService;
import com.project.Task.tracer.testContainer.PostgresContainer;
import com.project.Task.tracer.utils.BeanUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Tests for DialogService")
public class TaskServiceTests extends PostgresContainer {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserService userService;

    @Mock
    private User user = User.builder()
            .id(UUID.randomUUID())
            .roles(Collections.singletonList(Role.from(RoleType.ADMIN)))
            .build();

    @InjectMocks
    private TaskService taskService;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test getTasksByAuthorId")
    public void testGetTasksByAuthorId() {
        UUID authorId = UUID.randomUUID();
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        User author = User.builder().id(authorId).build();

        Task task1 = Task.builder()
                .id(UUID.randomUUID())
                .author(author)
                .build();
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .author(author)
                .build();
        Task task3 = Task.builder()
                .id(UUID.randomUUID())
                .author(author)
                .build();

        List<Task> tasks = List.of(task1, task2, task3);

        TaskListResponse response = taskMapper.fromListToTaskListResponse(tasks);

        when(taskRepository.findAllByAuthor_Id(authorId, pageable)).thenReturn(tasks);
        when(taskMapper.fromListToTaskListResponse(tasks)).thenReturn(response);

        TaskListResponse result = taskService.getTasksByAuthorId(authorId, page, size);

        assertEquals(response, result);

        verify(taskRepository, times(1)).findAllByAuthor_Id(authorId, pageable);
        verify(taskMapper, times(2)).fromListToTaskListResponse(tasks);
    }

    @Test
    @DisplayName("Test getTasksByExecutorId")
    public void testGetTasksByExecutorId() {
        UUID executorId = UUID.randomUUID();
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        User executor = User.builder().id(executorId).build();

        Task task1 = Task.builder()
                .id(UUID.randomUUID())
                .executor(executor)
                .build();
        Task task2 = Task.builder()
                .id(UUID.randomUUID())
                .executor(executor)
                .build();
        Task task3 = Task.builder()
                .id(UUID.randomUUID())
                .executor(executor)
                .build();

        List<Task> tasks = List.of(task1, task2, task3);

        TaskListResponse response = taskMapper.fromListToTaskListResponse(tasks);

        when(taskRepository.findAllByExecutor_Id(executorId, pageable)).thenReturn(tasks);
        when(taskMapper.fromListToTaskListResponse(tasks)).thenReturn(response);

        TaskListResponse result = taskService.getTasksByExecutorId(executorId, page, size);

        assertEquals(response, result);

        verify(taskRepository, times(1)).findAllByExecutor_Id(executorId, pageable);
        verify(taskMapper, times(2)).fromListToTaskListResponse(tasks);
    }

    @Test
    @DisplayName("Test getTask")
    public void testGetTask() {
        UUID taskId = UUID.randomUUID();
        Task task = Task.builder()
                .id(taskId)
                .build();
        TaskResponse response = taskMapper.fromTaskToResponse(task);

        when(taskRepository.findById(taskId)).thenReturn(Optional.ofNullable(task));
        when(taskMapper.fromTaskToResponse(task)).thenReturn(response);

        TaskResponse result = taskService.getTask(taskId);

        assertEquals(response, result);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskMapper, times(2)).fromTaskToResponse(task);
    }

    @Test
    @DisplayName("Test createTask")
    public void testCreateTask() {
        TaskRequest request = TaskRequest.builder()
                .title("title")
                .description("desc")
                .authorId(user.getId())
                .executedId(user.getId())
                .build();

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .author(user)
                .executor(user)
                .build();

        TaskResponse response = taskMapper.fromTaskToResponse(task);

        when(taskMapper.fromRequestToTask(request)).thenReturn(task);
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTaskToResponse(task)).thenReturn(response);

        TaskResponse result = taskService.createTask(request);

        assertEquals(response, result);

        verify(taskMapper, times(1)).fromRequestToTask(request);
        verify(userService, times(2)).getUserById(user.getId());
        verify(taskRepository, times(1)).save(task);
        verify(taskMapper, times(2)).fromTaskToResponse(task);
    }

    @Test
    @DisplayName("Test updateTask")
    public void testUpdateTask() {
        UUID taskId = UUID.randomUUID();
        TaskRequest request = TaskRequest.builder()
                .title("new title")
                .description("new desc")
                .authorId(user.getId())
                .executedId(user.getId())
                .build();

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .title("title")
                .description("desc")
                .author(user)
                .executor(user)
                .build();

        Task updateTask = Task.builder()
                .id(UUID.randomUUID())
                .title("new title")
                .description("new desc")
                .author(user)
                .executor(user)
                .build();

        TaskResponse response = taskMapper.fromTaskToResponse(task);

        when(taskMapper.fromRequestToTask(request)).thenReturn(updateTask);
        when(taskRepository.findById(taskId)).thenReturn(Optional.ofNullable(task));

        BeanUtils.copyNonNullProperties(updateTask, task);

        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.fromTaskToResponse(task)).thenReturn(response);

        TaskResponse result = taskService.updateTask(taskId, request);

        assertEquals(response, result);

        verify(taskMapper, times(1)).fromRequestToTask(request);
        verify(taskRepository, times(1)).findById(taskId);
        verify(taskRepository, times(1)).save(task);
        verify(taskMapper, times(2)).fromTaskToResponse(task);
    }

    @Test
    @DisplayName("Test updateStatus")
    public void testUpdateStatus() {
        UUID taskId = UUID.randomUUID();
        Status status = Status.DONE;
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(UUID.randomUUID())
                .roles(Collections.singletonList(Role.from(RoleType.ADMIN)))
                .build();

        Task task = Task.builder()
                .id(UUID.randomUUID())
                .title("title")
                .description("desc")
                .status(Status.IN_PROGRESS)
                .author(user)
                .executor(user)
                .build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.ofNullable(task));
        mockStatic(AuthService.class);
        when(AuthService.getCurrentUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(user);

        task.setStatus(status);

        when(taskRepository.save(task)).thenReturn(task);

        taskService.updateStatus(taskId, status);

        verify(taskRepository, times(1)).findById(taskId);
        verify(userService, times(1)).getUserById(userId);
        verify(taskRepository, times(1)).save(task);
    }
}
