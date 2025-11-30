package com.taskmanager.task.mapper;

import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.task.model.request.CreateTaskRequest;
import com.taskmanager.task.model.request.UpdateTaskRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = {java.time.LocalDateTime.class})
public interface TaskBOMapper {

    @Mapping(target = "createdBy.id", source = "createdBy")
    TaskBase mapFromCreateTaskRequest(CreateTaskRequest createTaskRequest, String createdBy);

    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo.id", source = "updateTaskRequest.assignedTo")
    @Mapping(target = "updatedBy.id", source = "updatedBy")
    TaskBase mapFromUpdateTaskRequest(UpdateTaskRequest updateTaskRequest, String taskId, String updatedBy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskNumber", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromBO(TaskBase updatedTask, @MappingTarget TaskBase taskToBeUpdated);

    UserBase userToUserBase(User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "assignedTo.id", source = "userId")
    @Mapping(target = "updatedBy.id", source = "assignedBy")
    TaskBase mapForAssigningTask(String taskId, String userId, String assignedBy);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "updatedBy.id", source = "updatedBy")
    TaskBase mapForTaskStatusUpdate(String taskId, TaskStatus status, String updatedBy);
}
