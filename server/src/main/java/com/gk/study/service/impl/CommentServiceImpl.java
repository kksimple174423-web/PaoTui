package com.gk.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Comment;
import com.gk.study.entity.CreditRecord;
import com.gk.study.entity.User;
import com.gk.study.mapper.CommentMapper;
import com.gk.study.mapper.CreditRecordMapper;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    CommentMapper mapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CreditRecordMapper creditRecordMapper;

    @Override
    public List<Comment> getCommentList() {
        return mapper.getList();
    }

    @Override
    public void createComment(Comment comment) {
        comment.setCommentTime(String.valueOf(System.currentTimeMillis()));
        if (comment.getLikeCount() == null) {
            comment.setLikeCount("0");
        }
        if (comment.getScore() == null) {
            comment.setScore("5");
        }
        mapper.insert(comment);
        // 评价完成后累计被评价人的信用分
        updateCreditScore(comment);
    }

    /** 按评分调整信用分：5星+2，4星+1，3星不变，2星-1，1星-3 */
    @Override
    public void updateCreditScore(Comment comment) {
        if (comment.getToUserId() == null || comment.getScore() == null) {
            return;
        }
        int score = parseInt(comment.getScore(), 5);
        int change = 0;
        if (score >= 5) {
            change = 2;
        } else if (score == 4) {
            change = 1;
        } else if (score == 3) {
            change = 0;
        } else if (score == 2) {
            change = -1;
        } else {
            change = -3;
        }

        User user = userMapper.selectById(comment.getToUserId());
        if (user == null) {
            return;
        }
        int current = parseInt(user.getCreditScore(), 100);
        int after = Math.max(0, Math.min(150, current + change));
        user.setCreditScore(String.valueOf(after));
        userMapper.updateById(user);

        CreditRecord record = new CreditRecord();
        record.setUserId(comment.getToUserId());
        record.setChangeScore(String.valueOf(change));
        record.setScoreAfter(String.valueOf(after));
        record.setReason(change >= 0 ? "获得好评，信用分提升" : "收到差评，信用分扣减");
        record.setOrderId(comment.getOrderId());
        record.setCreateTime(String.valueOf(System.currentTimeMillis()));
        creditRecordMapper.insert(record);
    }

    @Override
    public void deleteComment(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateComment(Comment comment) {
        mapper.updateById(comment);
    }

    @Override
    public Comment getCommentDetail(String id) {
        return mapper.selectById(id);
    }

    @Override
    public List<Comment> getTaskCommentList(String taskId, String order) {
        return mapper.selectTaskCommentList(taskId, order);
    }

    @Override
    public List<Comment> getUserCommentList(String userId) {
        return mapper.selectUserCommentList(userId);
    }

    @Override
    public List<Comment> getUserReceivedCommentList(String userId) {
        return mapper.selectReceivedCommentList(userId);
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}