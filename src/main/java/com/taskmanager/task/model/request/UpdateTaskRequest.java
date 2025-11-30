package com.taskmanager.task.model.request;

import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class UpdateTaskRequest {

    private String title;

    private String description;

    private TaskStatus status;

    private Priority priority;

    private String assignedTo;

    private LocalDate dueDate;

}
