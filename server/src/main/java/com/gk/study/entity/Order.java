package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 接单订单实体
 * 一个跑腿任务被骑手接单后生成一条订单，记录接单→取件→送达→确认→结算全过程
 * 对应数据表 b_order
 */
@Data
@TableName("b_order")
public class Order implements Serializable {

    /** 订单状态与任务状态保持一致 */
    public static final String STATUS_ACCEPTED   = "1";
    public static final String STATUS_DELIVERING = "2";
    public static final String STATUS_ARRIVED    = "3";
    public static final String STATUS_FINISHED   = "4";
    public static final String STATUS_CANCELED   = "5";
    public static final String STATUS_DISPUTE    = "6";

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String taskId;          // 任务ID

    @TableField
    public String status;          // 订单状态

    @TableField
    public String orderTime;       // 订单创建时间

    @TableField
    public String payTime;         // 支付时间

    @TableField
    public String publisherId;     // 任务发布者

    @TableField
    public String runnerId;        // 接单骑手

    @TableField
    public String amount;          // 订单金额

    @TableField
    public String orderNumber;     // 订单编号

    @TableField
    public String acceptTime;      // 接单时间

    @TableField
    public String finishTime;      // 送达时间

    @TableField
    public String confirmTime;     // 确认完成时间

    @TableField
    public String cancelReason;    // 取消原因

    @TableField
    public String statusRemark;    // 状态说明

    @TableField
    public String receiverAddress;

    @TableField
    public String receiverName;

    @TableField
    public String receiverPhone;

    @TableField
    public String remark;

    // ==================== 非数据库字段 ====================

    @TableField(exist = false)
    public String username;       // 发布者用户名

    @TableField(exist = false)
    public String publisherName;  // 发布者昵称

    @TableField(exist = false)
    public String runnerName;     // 骑手昵称

    @TableField(exist = false)
    public String title;          // 任务标题

    @TableField(exist = false)
    public String cover;          // 任务图片

    @TableField(exist = false)
    public String price;          // 任务赏金

    @TableField(exist = false)
    public String pickupAddress;  // 取件地址

    @TableField(exist = false)
    public String deliveryAddress;// 送达地址
}