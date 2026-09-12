package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单双向评价实体
 * role=1 用户评价骑手；role=2 骑手评价用户
 * 对应数据表 b_comment
 */
@Data
@TableName("b_comment")
public class Comment implements Serializable {

    public static final String ROLE_USER_TO_RUNNER = "1";
    public static final String ROLE_RUNNER_TO_USER = "2";

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String orderId;        // 关联订单ID

    @TableField
    public String taskId;         // 关联任务ID

    @TableField
    public String fromUserId;     // 评价人ID

    @TableField
    public String toUserId;       // 被评价人ID

    @TableField
    public String content;        // 评价内容

    @TableField
    public String score;          // 评分 1-5 星

    @TableField
    public String role;           // 评价人角色 1用户评骑手 2骑手评用户

    @TableField
    public String reply;          // 被评价人回复

    @TableField
    public String likeCount;      // 点赞数

    @TableField
    public String commentTime;    // 评价时间

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    public String username;       // 评价人昵称

    @TableField(exist = false)
    public String toUsername;     // 被评价人昵称

    @TableField(exist = false)
    public String avatar;         // 评价人头像

    @TableField(exist = false)
    public String title;          // 任务标题

    @TableField(exist = false)
    public String cover;          // 任务图片
}