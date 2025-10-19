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
import com.taskmanager.task.model.response.GetAllTasksResponse;
import com.taskmanager.task.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        TaskBase taskBase = taskBOMapper.mapFromCreateTaskRequest(createTaskRequest);
        taskBase = taskService.createTask(taskBase);
        return new ServiceResponse().build("Task is Created", HttpStatus.CREATED, taskBase);
    }

    @PostMapping(path = CommonConstants.API_SEARCH)
    public ResponseEntity<GetAllTasksResponse> searchTasks(@RequestBody TaskSearchRequest taskSearchRequest, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.getRoleList(Role.values()));
        GetAllTasksResponse response = new GetAllTasksResponse(taskService.searchTasks(taskSearchRequest));
        return response.build("Tasks Retrieved", HttpStatus.OK, response);
    }

}
