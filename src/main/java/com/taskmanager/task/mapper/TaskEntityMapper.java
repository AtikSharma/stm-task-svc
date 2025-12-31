package com.taskmanager.task.mapper;

import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.entity.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskEntityMapper {

    TaskBase mapFrom(TaskEntity taskEntity);

    TaskEntity mapTo(TaskBase taskBase);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskNumber", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    TaskEntity mapToForUpdate(TaskBase taskBase, @MappingTarget TaskEntity taskEntity);

}
