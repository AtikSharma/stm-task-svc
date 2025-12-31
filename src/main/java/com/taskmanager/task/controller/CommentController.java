package com.taskmanager.task.controller;

import com.taskmanager.common.RequestContext;
import com.taskmanager.common.constants.CommonConstants;
import com.taskmanager.common.constants.JwtConstants;
import com.taskmanager.common.enums.Role;
import com.taskmanager.common.model.CommentBase;
import com.taskmanager.common.model.ServiceResponse;
import com.taskmanager.common.util.JwtUtils;
import com.taskmanager.task.mapper.CommentBOMapper;
import com.taskmanager.task.model.request.CreateUpdateCommentRequest;
import com.taskmanager.task.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(CommonConstants.BASE_URL_COMMENT_V1)
public class CommentController {

    private final JwtUtils jwtUtils;
    private final CommentBOMapper commentBOMapper;
    private final CommentService commentService;

    @Autowired
    public CommentController(JwtUtils jwtUtils, CommentBOMapper commentBOMapper, CommentService commentService) {
        this.jwtUtils = jwtUtils;
        this.commentBOMapper = commentBOMapper;
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentBase> addComment(@RequestBody CreateUpdateCommentRequest createCommentRequest, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.getNonAdminRoles());
        String userId = jwtUtils.extractUserIdFromToken(authorizationHeader);
        CommentBase commentBase = commentBOMapper.mapFromCommentRequest(createCommentRequest,null);
        commentBase.setCreatedBy(userId);
        commentBase = commentService.addComment(commentBase);
        return new ServiceResponse().build("Comment Added", HttpStatus.CREATED, commentBase);
    }

    @PutMapping(path = CommonConstants.PATH_VARIABLE_COMMENT_ID)
    public ResponseEntity<CommentBase> updateComment(@RequestBody CreateUpdateCommentRequest updateCommentRequest, @PathVariable String commentId, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.getNonAdminRoles());
        String userId = jwtUtils.extractUserIdFromToken(authorizationHeader);
        CommentBase commentBase = commentBOMapper.mapFromCommentRequest(updateCommentRequest, commentId);
        commentBase.setUpdatedBy(userId);
        commentBase = commentService.updateComment(commentBase);
        return new ServiceResponse().build("Comment Updated", HttpStatus.OK, commentBase);
    }


    @DeleteMapping(path = CommonConstants.PATH_VARIABLE_COMMENT_ID)
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId, @RequestHeader(name = RequestContext.HEADER_FIELD_AUTHORIZATION, defaultValue = JwtConstants.DEFAULT_AUTHORIZATION, required = true) String authorizationHeader) {
        jwtUtils.validateAccess(authorizationHeader, Role.getNonAdminRoles());
        String userId = jwtUtils.extractUserIdFromToken(authorizationHeader);
        commentService.deleteComment(commentId, userId);
        return new ServiceResponse().build("Comment Deleted", HttpStatus.NO_CONTENT, null);
    }


}
