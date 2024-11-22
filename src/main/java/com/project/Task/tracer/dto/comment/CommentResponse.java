package com.project.Task.tracer.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CommentResponse {

    private UUID id;

    private LocalDateTime time;

    private String commentText;

}
