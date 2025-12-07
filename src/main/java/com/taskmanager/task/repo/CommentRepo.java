package com.taskmanager.task.repo;

import com.taskmanager.task.entity.CommentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepo extends MongoRepository<CommentEntity, String> {
}
