package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 骑手认证申请
 * 对应数据表 b_runner_verify
 */
@Data
@TableName("b_runner_verify")
public class RunnerVerify implements Serializable {

    /** 审核状态：0待审核 1已通过 2已驳回 */
    public static final String STATUS_WAITING = "0";
    public static final String STATUS_PASS    = "1";
    public static final String STATUS_REJECT  = "2";

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String userId;       // 申请人

    @TableField
    public String realName;     // 真实姓名

    @TableField
    public String studentNo;    // 学号

    @TableField
    public String college;      // 院系

    @TableField
    public String mobile;       // 联系电话

    @TableField
    public String cardImage;    // 学生证/证件照

    @TableField
    public String serviceArea;  // 服务区域

    @TableField
    public String serviceTime;  // 服务时段

    @TableField
    public String status;       // 审核状态

    @TableField
    public String auditRemark;  // 审核意见

    @TableField
    public String createTime;

    @TableField
    public String auditTime;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    public MultipartFile cardFile;   // 上传的证件照

    @TableField(exist = false)
    public String username;

    @TableField(exist = false)
    public String nickname;
}