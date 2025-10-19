package com.taskmanager.task.service.impl;

import com.taskmanager.common.client.UserServiceClient;
import com.taskmanager.common.enums.Priority;
import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.task.dao.TaskDao;
import com.taskmanager.task.model.request.TaskSearchRequest;
import com.taskmanager.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;
    private final UserServiceClient userServiceClient;

    @Autowired
    public TaskServiceImpl(TaskDao taskDao, UserServiceClient userServiceClient) {
        this.taskDao = taskDao;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public TaskBase createTask(TaskBase task) {
        LocalDateTime now = LocalDateTime.now();
        task.setTaskNumber((long) (100000 + new Random().nextInt(900000)));
        task.setStatus(TaskStatus.TO_DO);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task = taskDao.createTask(task);
        updateTaskUserDetails(task, getUsersMap());
        return task;
    }

    @Override
    public void assignTaskToUser(String taskId, String userId) {


    }

    @Override
    public void updateTaskPriority(String taskId, Priority priority) {

    }

    @Override
    public void updateTaskStatus(String taskId, TaskStatus status) {

    }

    @Override
    public TaskBase updateTask(TaskBase task) {
        return null;
    }

    @Override
    public void deleteTask(String taskId) {

    }

    @Override
    public List<TaskBase> searchTasks(TaskSearchRequest request) {
        Page<TaskBase> tasks = taskDao.searchTasks(request);
        Map<String, UserBase> usersMap = getUsersMap();
        tasks.forEach(task -> updateTaskUserDetails(task, usersMap));
        return tasks.getContent();
    }


    private TaskBase getTaskById(String taskId) {
        Optional<TaskBase> task = taskDao.getTaskById(taskId);
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task with ID " + taskId + " not found.");
        }
        return task.get();
    }


    private Map<String, UserBase> getUsersMap() {
        List<User> users = userServiceClient.getAllUsers();
        return users.stream().collect(Collectors.toMap(UserBase::getId, Function.identity()));
    }

    private void updateTaskUserDetails(TaskBase task, Map<String, UserBase> usersMap) {
        String createdBy = task.getCreatedBy().getId();
        String assignedTo = Optional.ofNullable(task.getAssignedTo())
                .map(UserBase::getId)
                .orElse(null);

        if (usersMap.containsKey(createdBy)) {
            task.setCreatedBy(usersMap.get(createdBy));
        }
        if (assignedTo != null && usersMap.containsKey(assignedTo)) {
            task.setAssignedTo(usersMap.get(assignedTo));
        }
    }
}
