package com.taskmanager.task.repo;

import com.taskmanager.task.entity.TaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends MongoRepository<TaskEntity, String>, QueryByExampleExecutor<TaskEntity> {

}
