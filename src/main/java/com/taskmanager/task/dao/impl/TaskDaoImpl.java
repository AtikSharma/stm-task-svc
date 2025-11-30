package com.taskmanager.task.dao.impl;

import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.dao.TaskDao;
import com.taskmanager.task.entity.TaskEntity;
import com.taskmanager.task.mapper.TaskEntityMapper;
import com.taskmanager.task.model.request.TaskSearchRequest;
import com.taskmanager.task.repo.TaskRepo;
import com.taskmanager.task.repo.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TaskDaoImpl implements TaskDao {

    private final TaskRepo taskRepo;
    private final TaskEntityMapper taskEntityMapper;

    @Autowired
    public TaskDaoImpl(TaskRepo taskRepo, TaskEntityMapper taskEntityMapper) {
        this.taskRepo = taskRepo;
        this.taskEntityMapper = taskEntityMapper;
    }

    @Override
    public TaskBase createTask(TaskBase task) {
        TaskEntity taskEntity = taskEntityMapper.mapTo(task);
        TaskEntity savedEntity = taskRepo.save(taskEntity);
        return taskEntityMapper.mapFrom(savedEntity);
    }

    @Override
    public Page<TaskBase> searchTasks(TaskSearchRequest request) {
        Specification<TaskEntity> spec = TaskSpecification.build(request);

        Sort sort = null;
        Pageable pageable = null;

        if (request.getSortDirection() != null && request.getSortBy() != null) {
            sort = Sort.by(request.getSortDirection(), request.getSortBy());
        }

        if (request.getPage() != null && request.getSize() != null && sort != null) {
            pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        } else if (request.getPage() != null && request.getSize() != null) {
            pageable = PageRequest.of(request.getPage(), request.getSize());
        } else if (sort != null) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sort); // default to first page with max size
        } else {
            pageable = Pageable.unpaged();
        }

        Page<TaskEntity> result = taskRepo.findAll(spec, pageable);

        return result.map(taskEntityMapper::mapFrom);
    }

    @Override
    public Optional<TaskBase> getTaskById(String taskId) {
        return taskRepo.findById(taskId).map(taskEntityMapper::mapFrom);
    }

    @Override
    public TaskBase updateTask(TaskBase taskToBeUpdated) {
        TaskEntity existingEntity = taskRepo.findById(taskToBeUpdated.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskToBeUpdated.getId()));
        TaskEntity taskEntity = taskEntityMapper.mapToForUpdate(taskToBeUpdated,existingEntity);
        TaskEntity savedEntity = taskRepo.save(taskEntity);
        return taskEntityMapper.mapFrom(savedEntity);
    }

    @Override
    public void deleteTask(String taskId) {
        taskRepo.deleteById(taskId);
    }
}
