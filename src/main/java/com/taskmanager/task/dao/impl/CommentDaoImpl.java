package com.taskmanager.task.dao.impl;

import com.taskmanager.common.model.CommentBase;
import com.taskmanager.task.dao.CommentDao;
import com.taskmanager.task.entity.CommentEntity;
import com.taskmanager.task.mapper.CommentEntityMapper;
import com.taskmanager.task.repo.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentDaoImpl implements CommentDao {

    private final CommentRepo commentRepo;
    private final CommentEntityMapper commentEntityMapper;

    @Autowired
    public CommentDaoImpl(CommentRepo commentRepo, CommentEntityMapper commentEntityMapper) {
        this.commentRepo = commentRepo;
        this.commentEntityMapper = commentEntityMapper;
    }

    @Override
    public CommentBase addComment(CommentBase newComment) {
        CommentEntity commentEntity = commentEntityMapper.toCommentEntity(newComment);
        commentEntity = commentRepo.save(commentEntity);
        return commentEntityMapper.fromCommentEntity(commentEntity);
    }

    @Override
    public CommentBase updateComment(CommentBase commentToBeUpdated) {
        CommentEntity commentEntity = commentEntityMapper.toCommentEntity(commentToBeUpdated);
        commentEntity = commentRepo.save(commentEntity);
        return commentEntityMapper.fromCommentEntity(commentEntity);
    }

    @Override
    public void deleteComment(CommentBase commentToBeDeleted) {
        CommentEntity commentEntity = commentEntityMapper.mapForDelete(commentToBeDeleted);
        commentRepo.save(commentEntity);
    }

    @Override
    public CommentBase getCommentById(String commentId) {
        CommentEntity commentEntity = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
        return commentEntityMapper.fromCommentEntity(commentEntity);
    }

    @Override
    public List<CommentBase> getCommentsByTaskId(String taskId) {
        List<CommentEntity> commentEntities = commentRepo.findByTaskIdAndIsDeletedFalse(taskId);
        return commentEntities.stream()
                .map(commentEntityMapper::fromCommentEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentsByTaskId(String taskId) {
        commentRepo.deleteByTaskId(taskId);
    }
}
