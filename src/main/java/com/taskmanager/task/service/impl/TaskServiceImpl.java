package com.taskmanager.task.service.impl;

import com.taskmanager.common.client.UserServiceClient;
import com.taskmanager.common.enums.Role;
import com.taskmanager.common.enums.TaskStatus;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.model.User;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.common.model.response.TaskResponse;
import com.taskmanager.common.model.response.UserResponse;
import com.taskmanager.task.dao.TaskDao;
import com.taskmanager.task.mapper.TaskBOMapper;
import com.taskmanager.task.model.request.TaskSearchRequest;
import com.taskmanager.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        task = taskDao.createTask(task);
        return task;
    }

    @Override
    public void assignTaskToUser(TaskBase taskToBeAssigned) {
        updateTask(taskToBeAssigned);
    }

    @Override
    public void updateTaskStatus(TaskBase taskToBeUpdated) {
        TaskBase taskToBeAssigned = getTaskById(taskToBeUpdated.getId());
        validateTaskAssignedToUser(taskToBeAssigned, taskToBeUpdated.getUpdatedBy());
        taskToBeAssigned = taskToBeAssigned.toBuilder().status(taskToBeUpdated.getStatus()).updatedBy(taskToBeUpdated.getUpdatedBy()).build();
        taskDao.updateTask(taskToBeAssigned);
    }

    @Override
    public TaskBase updateTask(TaskBase updatedTask) {
        TaskBase taskToBeUpdated = getTaskById(updatedTask.getId());
        taskBOMapper.updateTaskFromBO(updatedTask, taskToBeUpdated);
        return taskDao.updateTask(taskToBeUpdated);
    }

    @Override
    @Transactional
    public void deleteTask(String taskId) {
        getTaskById(taskId);
        taskDao.deleteTask(taskId);
    }

    @Override
    public List<TaskBase> searchTasks(TaskSearchRequest request) {
        Page<TaskBase> tasks = taskDao.searchTasks(request);
        return tasks.getContent();
    }

    @Override
    public TaskResponse buildResponse(TaskBase taskBase) {
        TaskResponse taskResponse = taskBOMapper.mapToTaskResponse(taskBase);
        Map<String, UserResponse> usersResponseMap = getUsersResponseMap();
        updateUserDetails(taskResponse, usersResponseMap);
        return taskResponse;
    }

    @Override
    public List<TaskResponse> buildResponse(List<TaskBase> taskBaseList) {
        List<TaskResponse> taskResponseList = taskBaseList.stream().map(taskBOMapper::mapToTaskResponse).collect(Collectors.toList());
        Map<String, UserResponse> usersResponseMap = getUsersResponseMap();
        taskResponseList.forEach(taskResponse -> {
            updateUserDetails(taskResponse, usersResponseMap);
        });
        return taskResponseList;
    }


    // Validate if the task is assigned to the user trying to update the status
    private void validateTaskAssignedToUser(TaskBase taskToBeAssigned, String updatedBy) {
        String assignedTo = taskToBeAssigned.getAssignedTo();

        User user = userServiceClient.getUserDetailsById(updatedBy, true);

        if (user == null) {
            throw new IllegalArgumentException("User with ID " + updatedBy + " not found.");
        } else if (!user.getRole().equals(Role.MANAGER)) {
            if (assignedTo == null || !assignedTo.equals(updatedBy)) {
                throw new IllegalArgumentException("Task with ID " + taskToBeAssigned.getId() + " is not assigned to user with ID " + updatedBy + ".");
            }
        }

    }

    private TaskBase getTaskById(String taskId) {
        Optional<TaskBase> task = taskDao.getTaskById(taskId);
        if (task.isEmpty()) {
            throw new IllegalArgumentException("Task with ID " + taskId + " not found.");
        }
        return task.get();
    }


    private Map<String, UserBase> getUsersMap() {
        List<User> users = userServiceClient.getAllUsers(false);
        return users.stream().map(taskBOMapper::userToUserBase).collect(Collectors.toMap(UserBase::getId, Function.identity()));
    }

    private Map<String, UserResponse> getUsersResponseMap() {
        List<User> users = userServiceClient.getAllUsers(false);
        return users.stream().map(taskBOMapper::userToUserResponse).collect(Collectors.toMap(UserResponse::getUserId, Function.identity()));
    }

    private void updateUserDetails(TaskResponse task, Map<String, UserResponse> usersMap) {
        String createdBy = Optional.ofNullable(task.getCreatedBy())
                .map(UserResponse::getUserId)
                .orElse(null);
        String updatedBy = Optional.ofNullable(task.getUpdatedBy())
                .map(UserResponse::getUserId)
                .orElse(null);
        String assignedTo = Optional.ofNullable(task.getAssignedTo())
                .map(UserResponse::getUserId)
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

        if (task.getComments() != null && !task.getComments().isEmpty()) {
            task.getComments().forEach(comment -> {
                String commentedBy = comment.getCommentedBy().getUserId();
                if (commentedBy != null && usersMap.containsKey(commentedBy)) {
                    comment.setCommentedBy(usersMap.get(commentedBy));
                }
            });
        }
    }
}
