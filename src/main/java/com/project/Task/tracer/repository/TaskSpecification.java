package com.project.Task.tracer.repository;

import com.project.Task.tracer.dto.task.TaskFilterRequest;
import com.project.Task.tracer.model.task.Priority;
import com.project.Task.tracer.model.task.Status;
import com.project.Task.tracer.model.task.Task;
import org.springframework.data.jpa.domain.Specification;

public interface TaskSpecification {

    static Specification<Task> findWithFilter(TaskFilterRequest filter) {
        return Specification.where(byTitle(filter.getTitle()))
                .and(byDescription(filter.getDescription()))
                .and(byStatus(filter.getStatus()))
                .and(byPriority(filter.getPriority()));
    }

    static Specification<Task> byTitle(String title) {
        return (root, query, criteriaBuilder) -> {
            if (title == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("title"), title);
        };
    }

    static Specification<Task> byDescription(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("description"), description);
        };
    }

    static Specification<Task> byStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status.name());
        };
    }

    static Specification<Task> byPriority(Priority priority) {
        return (root, query, criteriaBuilder) -> {
            if (priority == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("priority"), priority.name());
        };
    }
}
