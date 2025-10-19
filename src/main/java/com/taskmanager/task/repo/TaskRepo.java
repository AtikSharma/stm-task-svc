package com.taskmanager.task.repo;

import com.taskmanager.common.repo.SearchableEntityRepo;
import com.taskmanager.task.entity.TaskEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends SearchableEntityRepo<TaskEntity, String> {

}
