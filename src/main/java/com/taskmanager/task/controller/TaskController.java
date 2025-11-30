package com.taskmanager.task.controller;

import com.taskmanager.common.RequestContext;
import com.taskmanager.common.constants.CommonConstants;
import com.taskmanager.common.constants.JwtConstants;
import com.taskmanager.common.enums.Role;
import com.taskmanager.common.model.ServiceResponse;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.common.util.JwtUtils;
import com.taskmanager.task.mapper.TaskBOMapper;
import com.taskmanager.task.model.request.CreateTaskRequest;
import com.taskmanager.task.model.request.TaskSearchRequest;
import com.taskmanager.task.model.request.UpdateTaskRequest;
import com.taskmanager.task.model.request.UpdateTaskStatusRequest;
import com.taskmanager.task.model.response.GetAllTasksResponse;
import com.taskmanager.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(CommonConstants.BASE_URL_TASK_V1)
public class TaskController {

    private final JwtUtils jwtUtils;
    private final TaskBOMapper taskBOMapper;
    private final TaskService taskService;

    @Autowired
    public TaskController(JwtUtils jwtUtils, TaskBOMapper taskBOMapper, TaskService taskService) {
        this.jwtUtils = jwtUtils;
        this.taskBOMapper = taskBOMapper;
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskBase> createTask(@RequestBody CreateTaskRequest createTaskRequest, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.MANAGER);
        String userId = jwtUtils.extractUserIdFromToken(authorizationHeader);
        TaskBase taskBase = taskBOMapper.mapFromCreateTaskRequest(createTaskRequest, userId);
        taskBase = taskService.createTask(taskBase);
        return new ServiceResponse().build("Task is Created", HttpStatus.CREATED, taskBase);
    }

    @PostMapping(path = CommonConstants.API_SEARCH)
    public ResponseEntity<GetAllTasksResponse> searchTasks(@RequestBody TaskSearchRequest taskSearchRequest, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.getRoleList(Role.values()));
        GetAllTasksResponse response = new GetAllTasksResponse(taskService.searchTasks(taskSearchRequest));
        return response.build("Tasks Retrieved", HttpStatus.OK, response);
    }

    @PutMapping(path = CommonConstants.TASK_ID_PATH)
    public ResponseEntity<TaskBase> updateTask(@RequestBody UpdateTaskRequest updateTaskRequest, @PathVariable String taskId, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.MANAGER);
        String updatedBy = jwtUtils.extractUserIdFromToken(authorizationHeader);
        TaskBase taskBase = taskBOMapper.mapFromUpdateTaskRequest(updateTaskRequest, taskId, updatedBy);
        taskBase = taskService.updateTask(taskBase);
        return new ServiceResponse().build("Task is updated", HttpStatus.ACCEPTED, taskBase);
    }

    @DeleteMapping(path = CommonConstants.TASK_ID_PATH)
    public ResponseEntity<String> deleteTask(@PathVariable String taskId, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.MANAGER);
        taskService.deleteTask(taskId);
        return new ServiceResponse().build("Task is deleted", HttpStatus.OK, "Task Deleted Successfully");
    }

    @PatchMapping(path = CommonConstants.ASSIGNED_TO_USER_API)
    public ResponseEntity<String> assignTask(@PathVariable String taskId, @PathVariable String userId, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.MANAGER);
        String assignedBy = jwtUtils.extractUserIdFromToken(authorizationHeader);
        TaskBase taskToBeAssigned = taskBOMapper.mapForAssigningTask(taskId, userId, assignedBy);
        taskService.assignTaskToUser(taskToBeAssigned);
        return new ServiceResponse().build("Task is assigned to user", HttpStatus.OK, "Task Assigned Successfully");
    }

    @PatchMapping(path = CommonConstants.UPDATE_STATUS_API)
    public ResponseEntity<String> updateTaskStatus(@PathVariable String taskId, @RequestBody UpdateTaskStatusRequest request, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.getRoleList(Role.MANAGER, Role.USER));
        String updatedBy = jwtUtils.extractUserIdFromToken(authorizationHeader);
        TaskBase taskToBeUpdated = taskBOMapper.mapForTaskStatusUpdate(taskId,  request.getStatus(), updatedBy);
        taskService.updateTaskStatus(taskToBeUpdated);
        return new ServiceResponse().build("Task status is updated", HttpStatus.OK, "Task Status Updated Successfully");
    }

}
