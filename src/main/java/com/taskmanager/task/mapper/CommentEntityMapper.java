package com.taskmanager.task.mapper;

import com.taskmanager.common.model.CommentBase;
import com.taskmanager.task.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentEntityMapper {

    CommentEntity toCommentEntity(CommentBase comment);

    CommentBase fromCommentEntity(CommentEntity commentEntity);

    @Mapping(target = "isDeleted", constant = "true")
    @Mapping(target = "deletedAt", expression = "java(java.time.LocalDateTime.now())")
    CommentEntity mapForDelete(CommentBase commentToBeDeleted);
}
