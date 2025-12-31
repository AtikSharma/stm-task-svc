package com.taskmanager.task.entity;

import com.taskmanager.common.model.AuditEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class CommentEntity extends AuditEntity {

    @Id
    private String id;

    @NotNull
    private String taskId;

    @NotNull
    private String commentedBy;

    @NotNull
    private String content;

    private boolean isDeleted = false;

    private LocalDateTime deletedAt;

    private String deletedBy;
}
