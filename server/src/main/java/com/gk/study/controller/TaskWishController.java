package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.TaskWish;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.TaskService;
import com.gk.study.service.TaskWishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/taskWish")
public class TaskWishController {

    private final static Logger logger = LoggerFactory.getLogger(TaskWishController.class);

    @Autowired
    TaskWishService taskWishService;

    @Autowired
    TaskService taskService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/wish", method = RequestMethod.POST)
    @Transactional
    public APIResponse wish(TaskWish taskWish) throws IOException {
        if(taskWishService.getTaskWish(taskWish.getUserId(), taskWish.getTaskId()) != null){
            return new APIResponse(ResponeCode.SUCCESS, "您已添加过了");
        }else {
            taskWishService.createTaskWish(taskWish);
            taskService.addWishCount(taskWish.getTaskId());
        }
        return new APIResponse(ResponeCode.SUCCESS, "添加成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unWish", method = RequestMethod.POST)
    @Transactional
    public APIResponse unWish(String id) throws IOException {
        taskWishService.deleteTaskWish(id);
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    @RequestMapping(value = "/getUserWishList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserWishList(String userId) throws IOException {
        List<Map> lists = taskWishService.getTaskWishList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}
