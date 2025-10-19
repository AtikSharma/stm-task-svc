package com.taskmanager.task.service;

import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.model.request.TaskSearchRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    // Create a new task
    TaskBase createTask(TaskBase task);

    //Assign an existing task to a user
    void assignTaskToUser(String taskId, String userId);

    //Update the priority of an existing task
    void updateTaskPriority(String taskId, Priority priority);

    //Update the status of an existing task
    void updateTaskStatus(String taskId, TaskStatus status);

    //Update an existing task
    TaskBase updateTask(TaskBase task);

    //Delete an existing task and unassign it from any user
    void deleteTask(String taskId);

    //Search for tasks based on various criteria
    List<TaskBase> searchTasks(TaskSearchRequest taskSearchRequest);


}
