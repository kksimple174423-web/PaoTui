package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.List;

/**
 * 跑腿任务实体
 * 对应数据表 b_task
 */
@Data
@TableName("b_task")
public class Task implements Serializable {

    /** 任务状态：0待接单 1已接单 2配送中 3已送达(待确认) 4已完成 5已取消 6纠纷中 */
    public static final String STATUS_WAITING    = "0";
    public static final String STATUS_ACCEPTED   = "1";
    public static final String STATUS_DELIVERING = "2";
    public static final String STATUS_ARRIVED    = "3";
    public static final String STATUS_FINISHED   = "4";
    public static final String STATUS_CANCELED   = "5";
    public static final String STATUS_DISPUTE    = "6";

    @TableId(value = "id", type = IdType.AUTO)
    public Long id;

    @TableField
    public String title;              // 任务标题

    @TableField
    public String cover;              // 物品图片

    @TableField
    public String description;        // 任务描述

    @TableField
    public String reward;             // 悬赏金额

    @TableField
    public String pickupAddress;      // 取件地址

    @TableField
    public String deliveryAddress;    // 送达地址

    @TableField
    public String expectTime;         // 期望完成时间

    @TableField
    public String contactPhone;       // 联系电话

    @TableField
    public String weight;             // 物品规格/重量

    @TableField
    public String remark;             // 备注说明

    @TableField
    public String status;             // 任务状态

    @TableField
    public String createTime;         // 发布时间

    @TableField
    public String finishTime;         // 完成时间

    @TableField
    public String pv;                 // 浏览量

    @TableField
    public String recommendCount;     // 推荐数

    @TableField
    public String wishCount;          // 关注数

    @TableField
    public String collectCount;       // 收藏数

    @TableField
    public String orderCount;         // 接单人数

    @TableField
    public Long classificationId;     // 任务类型

    @TableField
    public Long publisherId;          // 发布者ID

    @TableField
    public Long runnerId;             // 接单骑手ID

    @TableField
    public String images;             // 多张物品图片(逗号分隔)

    // ==================== 非数据库字段（用于页面展示） ====================

    @TableField(exist = false)
    public List<Long> tags;           // 标签

    @TableField(exist = false)
    public MultipartFile imageFile;   // 上传的图片文件

    @TableField(exist = false)
    public String publisherName;      // 发布者昵称

    @TableField(exist = false)
    public String publisherAvatar;    // 发布者头像

    @TableField(exist = false)
    public String publisherCredit;    // 发布者信用分

    @TableField(exist = false)
    public String runnerName;         // 骑手昵称

    @TableField(exist = false)
    public String runnerAvatar;       // 骑手头像

    @TableField(exist = false)
    public String classificationTitle;// 任务类型名称

    /** 是否还在等待接单 */
    public boolean isWaiting() {
        return STATUS_WAITING.equals(status);
    }
}