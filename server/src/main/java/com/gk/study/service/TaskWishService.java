package com.gk.study.service;


import com.gk.study.entity.TaskWish;

import java.util.List;
import java.util.Map;

public interface TaskWishService {
    List<Map> getTaskWishList(String userId);
    void createTaskWish(TaskWish taskWish);
    void deleteTaskWish(String id);

    TaskWish getTaskWish(String userId, String taskId);
}
