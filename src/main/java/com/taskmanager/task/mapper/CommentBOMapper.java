package com.taskmanager.task.mapper;

import com.taskmanager.common.model.CommentBase;
import com.taskmanager.common.model.response.CommentResponse;
import com.taskmanager.task.model.request.CreateUpdateCommentRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CommentBOMapper {

    CommentBase mapFromCommentRequest(CreateUpdateCommentRequest createCommentRequest, String id);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "commentedBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CommentBase mapForUpdate(@MappingTarget CommentBase existingComment, CommentBase commentBase);

    @Mapping(target = "createdAt", defaultExpression =  "java(java.time.LocalDateTime.now())")
    @Mapping(target = "commentedBy", source = "createdBy")
    CommentBase mapForCreate(CommentBase commentBase);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "commentedBy.userId", source = "commentedBy")
    CommentResponse mapToCommentResponse(CommentBase commentBase);
}
