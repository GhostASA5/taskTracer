package com.project.Task.tracer.service;

import com.project.Task.tracer.dto.comment.CommentListResponse;
import com.project.Task.tracer.dto.comment.CommentRequest;
import com.project.Task.tracer.dto.comment.CommentResponse;
import com.project.Task.tracer.exception.ForbiddenException;
import com.project.Task.tracer.mapper.CommentMapper;
import com.project.Task.tracer.model.comment.Comment;
import com.project.Task.tracer.model.task.Task;
import com.project.Task.tracer.model.user.RoleType;
import com.project.Task.tracer.model.user.User;
import com.project.Task.tracer.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final TaskService taskService;

    private final UserService userService;

    public CommentListResponse getCommentsByTaskId(UUID taskId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return commentMapper.fromListToCommentListResponse(commentRepository.findAllByTaskId(taskId, pageable));
    }

    public CommentResponse addComment(UUID taskId, CommentRequest commentRequest) {
        Task task = taskService.getTaskById(taskId);
        UUID userId = AuthService.getCurrentUserId();
        User user = userService.getUserById(userId);

        if (!user.getRoles().get(0).getRole().equals(RoleType.ADMIN) && !task.getExecutor().getId().equals(userId)) {
            throw new ForbiddenException(MessageFormat.format(
                    "You don`t have the right to leave a comment. You are not the executor of the task {0}.", task.getId()
            ));
        }

        Comment comment = commentMapper.fromrequestToComment(commentRequest);
        comment.setTask(task);
        return commentMapper.fromCommentToResponse(commentRepository.save(comment));
    }
}
