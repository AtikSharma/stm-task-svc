package com.taskmanager.task.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    private String id;

    @NotNull
    private String taskId;

    @NotNull
    private String commentedBy;

    @NotNull
    private String content;

    private LocalDateTime createdAt;
}
