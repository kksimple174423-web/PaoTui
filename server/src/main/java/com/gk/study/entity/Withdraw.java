package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 提现申请
 * 对应数据表 b_withdraw
 */
@Data
@TableName("b_withdraw")
public class Withdraw implements Serializable {

    /** 状态：0待处理 1已打款 2已驳回 */
    public static final String STATUS_WAITING = "0";
    public static final String STATUS_PASS    = "1";
    public static final String STATUS_REJECT  = "2";

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String userId;

    @TableField
    public String amount;

    @TableField
    public String accountType;   // 支付宝/微信/银行卡

    @TableField
    public String account;

    @TableField
    public String realName;

    @TableField
    public String status;

    @TableField
    public String applyTime;

    @TableField
    public String handleTime;

    @TableField
    public String handleRemark;

    @TableField(exist = false)
    public String username;

    @TableField(exist = false)
    public String nickname;
}