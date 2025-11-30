package com.taskmanager.task.model.request;

import com.taskmanager.common.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class UpdateTaskStatusRequest {

    private TaskStatus status;
}
