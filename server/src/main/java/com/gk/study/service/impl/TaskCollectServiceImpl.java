package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.TaskCollectMapper;
import com.gk.study.service.TaskCollectService;
import com.gk.study.entity.TaskCollect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class TaskCollectServiceImpl extends ServiceImpl<TaskCollectMapper, TaskCollect> implements TaskCollectService {
    @Autowired
    TaskCollectMapper mapper;

    @Override
    public List<Map> getTaskCollectList(String userId) {
        return mapper.getTaskCollectList(userId);
    }

    @Override
    public void createTaskCollect(TaskCollect taskCollect) {
        mapper.insert(taskCollect);;
    }

    @Override
    public void deleteTaskCollect(String id) {
        mapper.deleteById(id);
    }

    @Override
    public TaskCollect getTaskCollect(String userId, String taskId) {
        QueryWrapper<TaskCollect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
}
