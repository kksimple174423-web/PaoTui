package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.TaskCollect;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.TaskCollectService;
import com.gk.study.service.TaskService;
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
@RequestMapping("/taskCollect")
public class TaskCollectController {

    private final static Logger logger = LoggerFactory.getLogger(TaskCollectController.class);

    @Autowired
    TaskCollectService taskCollectService;

    @Autowired
    TaskService taskService;

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    @Transactional
    public APIResponse collect(TaskCollect taskCollect) throws IOException {
        if(taskCollectService.getTaskCollect(taskCollect.getUserId(), taskCollect.getTaskId()) != null){
            return new APIResponse(ResponeCode.SUCCESS, "您已收藏过了");
        }else {
            taskCollectService.createTaskCollect(taskCollect);
            taskService.addCollectCount(taskCollect.getTaskId());
        }
        return new APIResponse(ResponeCode.SUCCESS, "收藏成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/unCollect", method = RequestMethod.POST)
    @Transactional
    public APIResponse unCollect(String id) throws IOException {
        taskCollectService.deleteTaskCollect(id);
        return new APIResponse(ResponeCode.SUCCESS, "取消收藏成功");
    }

    @RequestMapping(value = "/getUserCollectList", method = RequestMethod.GET)
    @Transactional
    public APIResponse getUserCollectList(String userId) throws IOException {
        List<Map> lists = taskCollectService.getTaskCollectList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", lists);
    }
}
