package com.gk.study.service;

import com.gk.study.entity.Task;

import java.util.List;

/**
 * 跑腿任务业务接口
 */
public interface TaskService {

    /** 任务大厅列表（支持关键词/排序/类型/标签/状态筛选） */
    List<Task> getTaskList(String keyword, String sort, String c, String tag, String status);

    /** 按接单状态查询（任务大厅：只显示可接单的） */
    List<Task> getHallList(String keyword, String c, String sort);

    Task getTaskById(String id);

    /** 发布任务 */
    void createTask(Task task);

    void updateTask(Task task);

    void deleteTask(String id);

    /** 骑手接单 */
    Task acceptTask(String taskId, String runnerId);

    /** 更新任务状态（配送中/已送达/已完成/已取消等） */
    void updateStatus(String taskId, String status);

    /** 我发布的任务 */
    List<Task> getMyPublishList(String userId, String status);

    /** 我接的任务 */
    List<Task> getMyAcceptList(String runnerId, String status);

    void addWishCount(String taskId);

    void addCollectCount(String taskId);

    /** 浏览量+1 */
    void addPv(String taskId);
}