package com.taskmanager.task.dao.impl;

import com.taskmanager.common.model.CommentBase;
import com.taskmanager.common.model.TaskBase;
import com.taskmanager.task.dao.CommentDao;
import com.taskmanager.task.dao.TaskDao;
import com.taskmanager.task.entity.TaskEntity;
import com.taskmanager.task.mapper.TaskEntityMapper;
import com.taskmanager.task.model.request.TaskSearchRequest;
import com.taskmanager.task.repo.TaskRepo;
import com.taskmanager.task.repo.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TaskDaoImpl implements TaskDao {

    private final TaskRepo taskRepo;
    private final TaskEntityMapper taskEntityMapper;
    private final MongoTemplate mongoTemplate;
    private final CommentDao commentDao;

    @Autowired
    public TaskDaoImpl(TaskRepo taskRepo, TaskEntityMapper taskEntityMapper, MongoTemplate mongoTemplate, CommentDao commentDao) {
        this.taskRepo = taskRepo;
        this.taskEntityMapper = taskEntityMapper;
        this.mongoTemplate = mongoTemplate;
        this.commentDao = commentDao;
    }

    @Override
    public TaskBase createTask(TaskBase task) {
        TaskEntity taskEntity = taskEntityMapper.mapTo(task);
        TaskEntity savedEntity = taskRepo.save(taskEntity);
        return taskEntityMapper.mapFrom(savedEntity);
    }

    @Override
    public Page<TaskBase> searchTasks(TaskSearchRequest request) {
        // determine sort and pageable
        Sort sort = null;
        Pageable pageable;

        if (request.getSortDirection() != null && request.getSortBy() != null) {
            sort = Sort.by(request.getSortDirection(), request.getSortBy());
        }

        if (request.getPage() != null && request.getSize() != null && sort != null) {
            pageable = PageRequest.of(request.getPage(), request.getSize(), sort);
        } else if (request.getPage() != null && request.getSize() != null) {
            pageable = PageRequest.of(request.getPage(), request.getSize());
        } else if (sort != null) {
            // when only sort is present, default to first page with max size (match provided snippet)
            pageable = PageRequest.of(0, Integer.MAX_VALUE, sort);
        } else {
            pageable = Pageable.unpaged();
        }

        // prepare query for fetching results
        Query fetchQuery = TaskSpecification.build(request);
        if (pageable.isPaged()) {
            fetchQuery.with(pageable);
        } else if (sort != null) {
            fetchQuery.with(sort);
        }

        List<TaskEntity> entities = mongoTemplate.find(fetchQuery, TaskEntity.class);

        long total = entities.size();

        // create Page object
        Page<TaskEntity> entityPage;
        if (pageable.isPaged()) {
            entityPage = new PageImpl<>(entities, pageable, total);
        } else {
            // unpaged: wrap results and provide total
            entityPage = new PageImpl<>(entities, Pageable.unpaged(), total);
        }

        // map entities to TaskBase
        List<TaskBase> mapped = entityPage.stream()
                .map(taskEntityMapper::mapFrom)
                .peek(this::loadCommentsForTask)
                .collect(Collectors.toList());

        return new PageImpl<>(mapped, entityPage.getPageable(), entityPage.getTotalElements());
    }

    @Override
    public Optional<TaskBase> getTaskById(String taskId) {
        return taskRepo.findById(taskId).map(task -> {
            TaskBase taskBase = taskEntityMapper.mapFrom(task);
            loadCommentsForTask(taskBase);
            return taskBase;
        });
    }

    @Override
    public TaskBase updateTask(TaskBase taskToBeUpdated) {
        TaskEntity existingEntity = taskRepo.findById(taskToBeUpdated.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskToBeUpdated.getId()));
        TaskEntity taskEntity = taskEntityMapper.mapToForUpdate(taskToBeUpdated, existingEntity);
        TaskEntity savedEntity = taskRepo.save(taskEntity);
        TaskBase taskBase = taskEntityMapper.mapFrom(savedEntity);
        loadCommentsForTask(taskBase);
        return taskBase;
    }

    @Override
    public void deleteTask(String taskId) {
        taskRepo.deleteById(taskId);
        commentDao.deleteCommentsByTaskId(taskId);
    }


    private void loadCommentsForTask(TaskBase taskBase) {
        List<CommentBase> comments = commentDao.getCommentsByTaskId(taskBase.getId());
        taskBase.setComments(comments);
    }
}
