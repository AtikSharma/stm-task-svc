package com.taskmanager.task.model.response;

import com.taskmanager.common.model.ServiceResponse;
import com.taskmanager.common.model.TaskBase;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class GetAllTasksResponse extends ServiceResponse {

    private List<TaskBase> tasks;

}
