package com.taskmanager.task.model.request;

import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.request.SearchRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class TaskSearchRequest extends SearchRequest {

    private String title;
    private Long taskNumber;
    private Priority priority;
    private TaskStatus status;
    private String assignedUserId;
    private String createdByUserId;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;

}
