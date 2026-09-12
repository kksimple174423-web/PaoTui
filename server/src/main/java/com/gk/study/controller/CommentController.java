package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Comment;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 订单双向评价接口
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    private final static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService service;

    /** 全部评价（管理端，用于内容审核） */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Comment> list = service.getCommentList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 某个任务下的所有评价 */
    @RequestMapping(value = "/listTaskComments", method = RequestMethod.GET)
    public APIResponse listTaskComments(String taskId, String order) {
        List<Comment> list = service.getTaskCommentList(taskId, order);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 我发出的评价 */
    @RequestMapping(value = "/listUserComments", method = RequestMethod.GET)
    public APIResponse listUserComments(String userId) {
        List<Comment> list = service.getUserCommentList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 我收到的评价 */
    @RequestMapping(value = "/listReceivedComments", method = RequestMethod.GET)
    public APIResponse listReceivedComments(String userId) {
        List<Comment> list = service.getUserReceivedCommentList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 提交评价（双向：role=1用户评骑手 role=2骑手评用户），提交后自动累计信用分 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Comment comment) throws IOException {
        service.createComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "评价成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteComment(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Comment comment) throws IOException {
        service.updateComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /** 被评价人回复 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    @Transactional
    public APIResponse reply(String id, String reply) throws IOException {
        Comment comment = new Comment();
        comment.setId(Long.valueOf(id));
        comment.setReply(reply);
        service.updateComment(comment);
        return new APIResponse(ResponeCode.SUCCESS, "回复成功");
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @Transactional
    public APIResponse like(String id) throws IOException {
        Comment commentBean = service.getCommentDetail(id);
        int likeCount = Integer.parseInt(commentBean.getLikeCount() == null ? "0" : commentBean.getLikeCount()) + 1;
        commentBean.setLikeCount(String.valueOf(likeCount));
        service.updateComment(commentBean);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

}