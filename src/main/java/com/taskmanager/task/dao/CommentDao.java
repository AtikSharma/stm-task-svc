package com.taskmanager.task.dao;

import com.taskmanager.common.model.CommentBase;

import java.util.List;

public interface CommentDao {

    CommentBase addComment(CommentBase newComment);

    CommentBase updateComment(CommentBase commentToBeUpdated);

    void deleteComment(CommentBase commentToBeDeleted);

    CommentBase getCommentById(String commentId);

    List<CommentBase> getCommentsByTaskId(String taskId);

    void deleteCommentsByTaskId(String taskId);
}
