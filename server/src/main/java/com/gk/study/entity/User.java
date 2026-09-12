package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 用户实体（普通用户 / 骑手 / 管理员统一账号）
 * 对应数据表 b_user
 */
@Data
@TableName("b_user")
public class User implements Serializable {

    public static final int NormalUser = 1;   // 普通用户
    public static final int DemoUser = 2;     // 演示账号
    public static final int AdminUser = 3;    // 管理员

    /** 骑手认证状态：0未申请 1待审核 2已认证 3已驳回 */
    public static final String RUNNER_NONE     = "0";
    public static final String RUNNER_AUDITING = "1";
    public static final String RUNNER_PASS     = "2";
    public static final String RUNNER_REJECT   = "3";

    @TableId(value = "id", type = IdType.AUTO)
    public String id;

    @TableField
    public String username;        // 登录账号

    @TableField
    public String password;        // 密码(MD5加盐)

    @TableField(exist = false)
    public String rePassword;      // 确认密码(不落库)

    @TableField
    public String studentNo;       // 学号

    @TableField
    public String nickname;        // 昵称

    @TableField
    public String realName;        // 真实姓名

    @TableField
    public String mobile;          // 手机号

    @TableField
    public String email;

    @TableField
    public String gender;          // 性别

    @TableField
    public String description;     // 个人简介

    @TableField
    public String role;            // 角色：1普通用户 >1管理员

    @TableField
    public String runnerStatus;    // 骑手认证状态

    @TableField
    public String creditScore;     // 信用分

    @TableField
    public String finishCount;     // 累计完成任务数

    @TableField
    public String totalIncome;     // 累计跑腿收入

    @TableField
    public String status;          // 账号状态

    @TableField
    public String avatar;          // 头像

    @TableField(exist = false)
    public MultipartFile avatarFile;

    @TableField
    public String token;           // 登录令牌

    @TableField
    public String createTime;

    @TableField
    public String pushEmail;

    @TableField
    public String pushSwitch;

    /** 是否已通过骑手认证 */
    public boolean isRunner() {
        return RUNNER_PASS.equals(runnerStatus);
    }
}