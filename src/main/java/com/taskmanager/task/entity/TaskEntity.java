package com.taskmanager.task.entity;

import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskEntity {

    @Id
    private String id;

    @Field
    private Long taskNumber;

    @Indexed(unique = true)
    private String title;

    private String description;

    private TaskStatus status;


    private Priority priority = Priority.MEDIUM;

    private LocalDate dueDate;

    @NotNull
    private String createdBy;

    private String assignedTo;

    private String updatedBy;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

}
