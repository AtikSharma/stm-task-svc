package com.taskmanager.task.mapper;

import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskEntityMapper {

    @Mapping(target = "createdBy.id", source = "createdBy")
    @Mapping(target = "assignedTo.id", source = "assignedTo")
    TaskBase mapFrom(TaskEntity taskEntity);

    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "assignedTo", source = "assignedTo.id")
    TaskEntity mapTo(TaskBase taskBase);
}
