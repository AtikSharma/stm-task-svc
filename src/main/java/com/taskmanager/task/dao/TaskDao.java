package com.taskmanager.task.dao;

import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.model.request.TaskSearchRequest;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TaskDao {

    TaskBase createTask(TaskBase task);

    Page<TaskBase> searchTasks(TaskSearchRequest taskSearchRequest);

    Optional<TaskBase> getTaskById(String taskId);
}
