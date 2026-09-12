package com.gk.study.service;


import com.gk.study.entity.TaskCollect;

import java.util.List;
import java.util.Map;

public interface TaskCollectService {
    List<Map> getTaskCollectList(String userId);
    void createTaskCollect(TaskCollect taskCollect);
    void deleteTaskCollect(String id);
    TaskCollect getTaskCollect(String userId, String taskId);
}
