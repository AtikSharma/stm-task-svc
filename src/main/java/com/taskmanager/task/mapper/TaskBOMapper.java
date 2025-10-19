package com.taskmanager.task.mapper;

import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.model.request.CreateTaskRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.scheduling.config.Task;

@Mapper(componentModel = "spring")
public interface TaskBOMapper {

    @Mapping(target = "createdBy.id", source = "createdBy")
    TaskBase mapFromCreateTaskRequest(CreateTaskRequest createTaskRequest);

    TaskBase mapFrom(Task task);
}
