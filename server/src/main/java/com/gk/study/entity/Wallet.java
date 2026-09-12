package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户钱包
 * 对应数据表 b_wallet
 */
@Data
@TableName("b_wallet")
public class Wallet implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String userId;

    @TableField
    public String balance;      // 可用余额

    @TableField
    public String frozen;       // 冻结金额（任务赏金托管中）

    @TableField
    public String totalIncome;  // 累计收入

    @TableField
    public String totalExpense; // 累计支出

    @TableField
    public String payPassword;

    @TableField
    public String createTime;

    @TableField
    public String updateTime;
}