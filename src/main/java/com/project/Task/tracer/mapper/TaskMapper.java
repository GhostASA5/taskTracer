package com.project.Task.tracer.mapper;

import com.project.Task.tracer.dto.task.TaskListResponse;
import com.project.Task.tracer.dto.task.TaskRequest;
import com.project.Task.tracer.dto.task.TaskResponse;
import com.project.Task.tracer.model.task.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task fromRequestToTask(TaskRequest taskRequest);

    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "executor.id", target = "executedId")
    TaskResponse fromTaskToResponse(Task task);

    default TaskListResponse fromListToTaskListResponse(List<Task> taskList) {
        TaskListResponse taskListResponse = new TaskListResponse();
        taskListResponse.setTasks(
                taskList.stream().map(this::fromTaskToResponse).collect(Collectors.toList())
        );
        return taskListResponse;
    }
}
