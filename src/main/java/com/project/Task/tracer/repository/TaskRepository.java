package com.project.Task.tracer.repository;

import com.project.Task.tracer.model.task.Task;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>, JpaSpecificationExecutor<Task> {

    List<Task> findAllByAuthor_Id(UUID authorId, Pageable pageable);

    List<Task> findAllByExecutor_Id(UUID executorId, Pageable pageable);
}
