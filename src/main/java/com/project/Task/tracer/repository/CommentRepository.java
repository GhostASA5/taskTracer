package com.project.Task.tracer.repository;

import com.project.Task.tracer.model.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByTaskId(UUID taskId, Pageable pageable);
}
