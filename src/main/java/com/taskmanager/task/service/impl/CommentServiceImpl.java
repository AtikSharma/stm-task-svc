package com.taskmanager.task.service.impl;

import com.taskmanager.common.model.CommentBase;
import com.taskmanager.common.model.UserBase;
import com.taskmanager.task.dao.CommentDao;
import com.taskmanager.task.mapper.CommentBOMapper;
import com.taskmanager.task.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final CommentBOMapper commentBOMapper;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, CommentBOMapper commentBOMapper) {
        this.commentDao = commentDao;
        this.commentBOMapper = commentBOMapper;
    }

    @Override
    public CommentBase addComment(CommentBase commentBase) {
        commentBase = commentBOMapper.mapForCreate(commentBase);
        commentBase = commentDao.addComment(commentBase);
        return commentBase;
    }

    @Override
    public CommentBase updateComment(CommentBase commentBase) {
        CommentBase existingComment = commentDao.getCommentById(commentBase.getId());
        existingComment = commentBOMapper.mapForUpdate(existingComment, commentBase);
        existingComment = commentDao.updateComment(existingComment);
        return existingComment;
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        CommentBase existingComment = commentDao.getCommentById(commentId);
        existingComment.setDeletedBy(userId);
        commentDao.deleteComment(existingComment);
    }
}
