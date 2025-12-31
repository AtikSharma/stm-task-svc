package com.taskmanager.task.service;

import com.taskmanager.common.model.CommentBase;

public interface CommentService {
    CommentBase addComment(CommentBase commentBase);

    CommentBase updateComment(CommentBase commentBase);

    void deleteComment(String commentId, String userId);
}
