package com.project.Task.tracer.controller;

import com.project.Task.tracer.dto.comment.CommentListResponse;
import com.project.Task.tracer.dto.comment.CommentRequest;
import com.project.Task.tracer.dto.comment.CommentResponse;
import com.project.Task.tracer.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{taskId}")
    public ResponseEntity<CommentListResponse> getComments(@PathVariable("taskId") UUID taskId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByTaskId(taskId, page, size));
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<CommentResponse> createComment(@PathVariable("taskId") UUID taskId,
                                                         @RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok(commentService.addComment(taskId, commentRequest));
    }
}
