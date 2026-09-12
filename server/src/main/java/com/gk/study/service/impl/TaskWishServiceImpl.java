package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.TaskWishMapper;
import com.gk.study.service.TaskWishService;
import com.gk.study.entity.TaskWish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
class TaskWishServiceImpl extends ServiceImpl<TaskWishMapper, TaskWish> implements TaskWishService {
    @Autowired
    TaskWishMapper mapper;

    @Override
    public List<Map> getTaskWishList(String userId) {
        return mapper.getTaskWishList(userId);
    }

    @Override
    public void createTaskWish(TaskWish taskWish) {
        mapper.insert(taskWish);;
    }

    @Override
    public void deleteTaskWish(String id) {
        mapper.deleteById(id);
    }

    @Override
    public TaskWish getTaskWish(String userId, String taskId) {
        QueryWrapper<TaskWish> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId)
                .eq("user_id", userId);
        return mapper.selectOne(queryWrapper);
    }
}
