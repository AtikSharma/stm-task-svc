package com.taskmanager.task.entity;

import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.AuditEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Document(collection = "tasks")
@NoArgsConstructor
public class TaskEntity extends AuditEntity {

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

    private String assignedTo;

}
