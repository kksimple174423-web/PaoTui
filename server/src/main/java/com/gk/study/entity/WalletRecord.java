package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 钱包资金流水
 * 对应数据表 b_wallet_record
 */
@Data
@TableName("b_wallet_record")
public class WalletRecord implements Serializable {

    /** 流水类型：1充值 2发布任务(托管) 3任务收入 4退款 5提现 6平台奖励 */
    public static final String TYPE_RECHARGE = "1";
    public static final String TYPE_TASK_PAY = "2";
    public static final String TYPE_TASK_INCOME = "3";
    public static final String TYPE_REFUND = "4";
    public static final String TYPE_WITHDRAW = "5";
    public static final String TYPE_BONUS = "6";

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String userId;

    @TableField
    public String type;

    @TableField
    public String amount;        // 变动金额（正数入账/负数出账）

    @TableField
    public String balanceAfter;

    @TableField
    public String taskId;

    @TableField
    public String orderId;

    @TableField
    public String remark;

    @TableField
    public String createTime;

    @TableField(exist = false)
    public String typeText;
}