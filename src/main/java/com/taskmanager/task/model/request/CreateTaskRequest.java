package com.taskmanager.task.model.request;

import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.model.UserBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class CreateTaskRequest {

    private String title;

    private String description;

    private Priority priority;

    private String createdBy;

    private LocalDate dueDate;

}
