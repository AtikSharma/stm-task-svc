package com.taskmanager.task.service.impl;

import com.taskmanager.common.client.UserServiceClient;
import com.taskmanager.common.enums.Role;
import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.task.dao.TaskDao;
import com.taskmanager.task.mapper.TaskBOMapper;
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
    private final TaskBOMapper taskBOMapper;

    @Autowired
    public TaskServiceImpl(TaskDao taskDao, UserServiceClient userServiceClient, TaskBOMapper taskBOMapper) {
        this.taskDao = taskDao;
        this.userServiceClient = userServiceClient;
        this.taskBOMapper = taskBOMapper;
    }

    @Override
    public TaskBase createTask(TaskBase task) {
        LocalDateTime now = LocalDateTime.now();
        task.setTaskNumber((long) (100000 + new Random().nextInt(900000)));
        task.setStatus(TaskStatus.TO_DO);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task = taskDao.createTask(task);
        updateTaskUserDetails(task, getUsersMap(false));
        return task;
    }

    @Override
    public void assignTaskToUser(TaskBase taskToBeAssigned) {
        updateTask(taskToBeAssigned);
    }

    @Override
    public void updateTaskStatus(TaskBase taskToBeUpdated) {
        TaskBase taskToBeAssigned = getTaskById(taskToBeUpdated.getId());
        validateTaskAssignedToUser(taskToBeAssigned, taskToBeUpdated.getUpdatedBy().getId());
        taskToBeAssigned = taskToBeAssigned.toBuilder().status(taskToBeUpdated.getStatus()).updatedBy(taskToBeUpdated.getUpdatedBy()).build();
        taskDao.updateTask(taskToBeAssigned);
    }

    // Validate if the task is assigned to the user trying to update the status
    private void validateTaskAssignedToUser(TaskBase taskToBeAssigned, String updatedBy) {
        String assignedTo = Optional.ofNullable(taskToBeAssigned.getAssignedTo())
                .map(UserBase::getId)
                .orElse(null);

        User user = userServiceClient.getUserDetailsById(updatedBy, true);

        if (user == null) {
            throw new IllegalArgumentException("User with ID " + updatedBy + " not found.");
        } else if (!user.getRole().equals(Role.MANAGER)) {
            if (assignedTo == null || !assignedTo.equals(updatedBy)) {
                throw new IllegalArgumentException("Task with ID " + taskToBeAssigned.getId() + " is not assigned to user with ID " + updatedBy + ".");
            }
        }

    }

    @Override
    public TaskBase updateTask(TaskBase updatedTask) {
        TaskBase taskToBeUpdated = getTaskById(updatedTask.getId());
        taskBOMapper.updateTaskFromBO(updatedTask, taskToBeUpdated);
        TaskBase savedTask = taskDao.updateTask(taskToBeUpdated);
        updateTaskUserDetails(savedTask, getUsersMap(false));
        return savedTask;
    }

    @Override
    public void deleteTask(String taskId) {
        getTaskById(taskId);
        taskDao.deleteTask(taskId);
    }

    @Override
    public List<TaskBase> searchTasks(TaskSearchRequest request) {
        Page<TaskBase> tasks = taskDao.searchTasks(request);
        Map<String, UserBase> usersMap = getUsersMap(false);
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


    private Map<String, UserBase> getUsersMap(boolean fetchSensitiveInfo) {
        List<User> users = userServiceClient.getAllUsers(fetchSensitiveInfo);
        return users.stream().map(taskBOMapper::userToUserBase).collect(Collectors.toMap(UserBase::getId, Function.identity()));
    }

    private void updateTaskUserDetails(TaskBase task, Map<String, UserBase> usersMap) {
        String createdBy = Optional.ofNullable(task.getCreatedBy())
                .map(UserBase::getId)
                .orElse(null);

        String updatedBy = Optional.ofNullable(task.getUpdatedBy())
                .map(UserBase::getId)
                .orElse(null);

        String assignedTo = Optional.ofNullable(task.getAssignedTo())
                .map(UserBase::getId)
                .orElse(null);

        if (assignedTo != null && usersMap.containsKey(assignedTo)) {
            task.setAssignedTo(usersMap.get(assignedTo));
        }

        if (createdBy != null && usersMap.containsKey(createdBy)) {
            task.setCreatedBy(usersMap.get(createdBy));
        }

        if (updatedBy != null && usersMap.containsKey(updatedBy)) {
            task.setUpdatedBy(usersMap.get(updatedBy));
        }
    }
}
