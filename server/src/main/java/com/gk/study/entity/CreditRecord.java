package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 信用分变更记录
 * 对应数据表 b_credit_record
 */
@Data
@TableName("b_credit_record")
public class CreditRecord implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String userId;       // 用户ID

    @TableField
    public String changeScore;  // 变动分值

    @TableField
    public String scoreAfter;   // 变动后信用分

    @TableField
    public String reason;       // 变动原因

    @TableField
    public String orderId;      // 关联订单

    @TableField
    public String createTime;
}