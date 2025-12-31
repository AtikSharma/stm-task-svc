package com.taskmanager.task.mapper;

import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.response.TaskResponse;
import com.taskmanager.common.model.response.UserResponse;
import com.taskmanager.task.model.request.CreateTaskRequest;
import com.taskmanager.task.model.request.UpdateTaskRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", imports = {java.time.LocalDateTime.class}, uses = {CommentBOMapper.class})
public interface TaskBOMapper {

    TaskBase mapFromCreateTaskRequest(CreateTaskRequest createTaskRequest, String createdBy);

    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo", source = "updateTaskRequest.assignedTo")
    TaskBase mapFromUpdateTaskRequest(UpdateTaskRequest updateTaskRequest, String taskId, String updatedBy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskNumber", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(LocalDateTime.now())")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTaskFromBO(TaskBase updatedTask, @MappingTarget TaskBase taskToBeUpdated);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
    UserResponse userToUserResponse(UserBase user);

    UserBase userToUserBase(User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "assignedTo", source = "userId")
    @Mapping(target = "updatedBy", source = "assignedBy")
    TaskBase mapForAssigningTask(String taskId, String userId, String assignedBy);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "taskId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "updatedBy", source = "updatedBy")
    TaskBase mapForTaskStatusUpdate(String taskId, TaskStatus status, String updatedBy);

    @Mapping(target = "taskId", source = "id")
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "mapStringToUserBase")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "mapStringToUserBase")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "mapStringToUserBase")
    @Mapping(target = "creationTime", source = "createdAt")
    @Mapping(target = "updateTime", source = "updatedAt")
    TaskResponse mapToTaskResponse(TaskBase taskBase);

    @Named(value = "mapStringToUserBase")
    default UserBase mapStringToUserBase(String userId) {
        if (userId == null) {
            return null;
        }
        UserBase userBase = new UserBase();
        userBase.setId(userId);
        return userBase;
    }

}
