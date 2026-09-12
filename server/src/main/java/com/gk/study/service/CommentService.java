package com.gk.study.service;

import com.gk.study.entity.Comment;

import java.util.List;

/**
 * 订单双向评价业务接口
 */
public interface CommentService {

    List<Comment> getCommentList();

    void createComment(Comment comment);

    void deleteComment(String id);

    void updateComment(Comment comment);

    Comment getCommentDetail(String id);

    /** 某个任务下的全部评价 */
    List<Comment> getTaskCommentList(String taskId, String order);

    /** 我发出的评价 */
    List<Comment> getUserCommentList(String userId);

    /** 我收到的评价 */
    List<Comment> getUserReceivedCommentList(String userId);

    /** 评价后累计被评价人的信用分 */
    void updateCreditScore(Comment comment);
}