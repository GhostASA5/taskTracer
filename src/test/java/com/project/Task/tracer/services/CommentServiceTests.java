package com.project.Task.tracer.services;

import com.project.Task.tracer.dto.comment.CommentListResponse;
import com.project.Task.tracer.dto.comment.CommentRequest;
import com.project.Task.tracer.dto.comment.CommentResponse;
import com.project.Task.tracer.mapper.CommentMapper;
import com.project.Task.tracer.model.comment.Comment;
import com.project.Task.tracer.model.task.Task;
import com.project.Task.tracer.model.user.Role;
import com.project.Task.tracer.model.user.RoleType;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.CommentRepository;
import com.project.Task.tracer.service.AuthService;
import com.project.Task.tracer.service.CommentService;
import com.project.Task.tracer.service.TaskService;
import com.project.Task.tracer.service.UserService;
import com.project.Task.tracer.testContainer.PostgresContainer;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yaml")
@DisplayName("Tests for CommentService")
public class CommentServiceTests extends PostgresContainer {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CommentService commentService;

    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    public static void afterAll() {
        postgres.stop();
    }

    @Test
    @DisplayName("Test getCommentsByTaskId")
    public void getCommentsByTaskId() {
        UUID taskId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        Comment comment1 = Comment.builder()
                .id(UUID.randomUUID())
                .commentText("Test text1")
                .task(null)
                .time(LocalDateTime.now())
                .build();
        Comment comment2 = Comment.builder()
                .id(UUID.randomUUID())
                .commentText("Test text2")
                .task(null)
                .time(LocalDateTime.now())
                .build();

        List<Comment> comments = List.of(comment1, comment2);
        CommentResponse commentResponse1 = commentMapper.fromCommentToResponse(comment1);
        CommentResponse commentResponse2 = commentMapper.fromCommentToResponse(comment2);
        CommentListResponse response = new CommentListResponse();
        response.setComments(Arrays.asList(commentResponse1, commentResponse2));

        when(commentRepository.findAllByTaskId(taskId, pageable)).thenReturn(comments);
        when(commentMapper.fromListToCommentListResponse(comments)).thenReturn(response);

        CommentListResponse result = commentService.getCommentsByTaskId(taskId, page, size);

        assertEquals(response, result);
        verify(commentRepository, times(1)).findAllByTaskId(taskId, pageable);
        verify(commentMapper, times(1)).fromListToCommentListResponse(comments);
    }

    @Test
    @DisplayName("Test addComment")
    public void addComment() {
        UUID taskId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        CommentRequest request = CommentRequest.builder()
                .commentText("Test new text")
                .build();

        Role role = Role.from(RoleType.ADMIN);
        User user = User.builder()
                .id(userId)
                .roles(Collections.singletonList(role))
                .build();
        role.setUser(user);

        Task task = Task.builder()
                .id(taskId)
                .build();

        UUID commentId = UUID.randomUUID();
        LocalDateTime time = LocalDateTime.now();
        Comment comment = Comment.builder()
                .id(commentId)
                .commentText("Test new text")
                .time(time)
                .task(task)
                .build();

        CommentResponse response = CommentResponse.builder()
                .id(commentId)
                .commentText("Test new text")
                .time(time)
                .build();

        when(taskService.getTaskById(taskId)).thenReturn(task);
        mockStatic(AuthService.class);
        when(AuthService.getCurrentUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(user);
        when(commentMapper.fromrequestToComment(request)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.fromCommentToResponse(comment)).thenReturn(response);

        CommentResponse result = commentService.addComment(taskId, request);

        assertEquals(response, result);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).fromrequestToComment(request);
        verify(commentMapper, times(1)).fromCommentToResponse(comment);
    }
}
