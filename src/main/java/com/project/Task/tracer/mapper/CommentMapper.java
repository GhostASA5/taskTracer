package com.project.Task.tracer.mapper;

import com.project.Task.tracer.dto.comment.CommentListResponse;
import com.project.Task.tracer.dto.comment.CommentRequest;
import com.project.Task.tracer.dto.comment.CommentResponse;
import com.project.Task.tracer.model.comment.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    Comment fromrequestToComment(CommentRequest request);

    CommentResponse fromCommentToResponse(Comment comment);

    default CommentListResponse fromListToCommentListResponse(List<Comment> comments) {
        CommentListResponse response = new CommentListResponse();
        response.setComments(comments.stream().map(this::fromCommentToResponse).collect(Collectors.toList()));
        return response;
    }
}
