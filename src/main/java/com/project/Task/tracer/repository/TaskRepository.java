package com.project.Task.tracer.repository;

import com.project.Task.tracer.model.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

}
