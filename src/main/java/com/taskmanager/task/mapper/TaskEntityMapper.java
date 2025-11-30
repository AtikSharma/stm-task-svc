package com.taskmanager.task.mapper;

import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.task.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface TaskEntityMapper {

    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "setUserDetails")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "setUserDetails")
    @Mapping(target = "assignedTo", source = "assignedTo", qualifiedByName = "setUserDetails")
    TaskBase mapFrom(TaskEntity taskEntity);

    @Mapping(target = "updatedBy", source = "updatedBy.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "assignedTo", source = "assignedTo.id")
    TaskEntity mapTo(TaskBase taskBase);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskNumber", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedBy", source = "updatedBy.id")
    @Mapping(target = "assignedTo", source = "assignedTo.id")
    TaskEntity mapToForUpdate(TaskBase taskBase, @MappingTarget TaskEntity taskEntity);

    @Named(value = "setUserDetails")
    default UserBase setUserDetails(String value) {
        if (value == null) {
            return null;
        }
        UserBase userBase = new UserBase();
        userBase.setId(value);
        return userBase;
    }
}
