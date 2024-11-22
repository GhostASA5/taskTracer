package com.project.Task.tracer.model.comment;

import com.project.Task.tracer.model.task.Task;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime time;

    @Column(name = "comment_text", columnDefinition = "TEXT")
    private String commentText;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
}
