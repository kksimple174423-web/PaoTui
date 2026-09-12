package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Task;
import com.gk.study.entity.User;
import com.gk.study.entity.Wallet;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.TaskService;
import com.gk.study.service.UserService;
import com.gk.study.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 跑腿任务接口：任务大厅、发布、详情、接单、状态流转
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    private final static Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    TaskService service;

    @Autowired
    UserService userService;

    @Autowired
    WalletService walletService;

    @Autowired
    HttpServletRequest request;

    @Value("${File.uploadPath}")
    private String uploadPath;

    /** 任务大厅列表：keyword 关键词 / sort 排序 / c 任务类型 / tag 标签 / status 状态 */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword, String sort, String c, String tag, String status) {
        List<Task> list = service.getTaskList(keyword, sort, c, tag, status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 任务大厅：只返回还在等待接单的任务 */
    @RequestMapping(value = "/hall", method = RequestMethod.GET)
    public APIResponse hall(String keyword, String sort, String c) {
        List<Task> list = service.getHallList(keyword, c, sort);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 任务详情 */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id) {
        Task task = service.getTaskById(id);
        if (task != null) {
            service.addPv(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", task);
    }

    /** 发布任务（登录用户即可发布，发布者取当前登录用户） */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Task task, String tags) throws IOException {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        // 赏金托管校验：余额必须足够支付悬赏金额
        Wallet wallet = walletService.getWallet(user.getId());
        BigDecimal reward = new BigDecimal(StringUtils.isEmpty(task.getReward()) ? "0" : task.getReward());
        if (new BigDecimal(wallet.getBalance()).compareTo(reward) < 0) {
            return new APIResponse(ResponeCode.FAIL, "钱包余额不足，请先充值后再发布任务");
        }

        task.setPublisherId(Long.valueOf(user.getId()));
        task.setRunnerId(null);

        String url = saveImage(task);
        if (!StringUtils.isEmpty(url)) {
            task.cover = url;
        }
        task.setTags(parseTags(tags));
        service.createTask(task);

        // 托管赏金：从可用余额转入冻结金额
        walletService.freeze(user.getId(), task.getReward(), String.valueOf(task.getId()));
        return new APIResponse(ResponeCode.SUCCESS, "发布成功，赏金已托管");
    }

    /** 编辑任务 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Task task, String tags) throws IOException {
        String url = saveImage(task);
        if (!StringUtils.isEmpty(url)) {
            task.cover = url;
        }
        if (StringUtils.hasText(tags)) {
            task.setTags(parseTags(tags));
        }
        service.updateTask(task);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /** 后台新增任务（管理员） */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/adminCreate", method = RequestMethod.POST)
    @Transactional
    public APIResponse adminCreate(Task task, String tags) throws IOException {
        if (task.getPublisherId() == null) {
            task.setPublisherId(null);
        }
        String url = saveImage(task);
        if (!StringUtils.isEmpty(url)) {
            task.cover = url;
        }
        task.setTags(parseTags(tags));
        service.createTask(task);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    /** 后台编辑任务（管理员） */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/adminUpdate", method = RequestMethod.POST)
    @Transactional
    public APIResponse adminUpdate(Task task, String tags) throws IOException {
        String url = saveImage(task);
        if (!StringUtils.isEmpty(url)) {
            task.cover = url;
        }
        if (StringUtils.hasText(tags)) {
            task.setTags(parseTags(tags));
        }
        service.updateTask(task);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /** 删除任务（管理员） */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteTask(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    /** 骑手接单 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    @Transactional
    public APIResponse accept(String taskId) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        Task task = service.acceptTask(taskId, user.getId());
        if (task == null) {
            return new APIResponse(ResponeCode.FAIL, "该任务已被接单或不可接取");
        }
        return new APIResponse(ResponeCode.SUCCESS, "接单成功", task);
    }

    /** 更新任务状态：1已接单 2配送中 3已送达 4已完成 5已取消 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateStatus(String taskId, String status) {
        service.updateStatus(taskId, status);
        return new APIResponse(ResponeCode.SUCCESS, "操作成功");
    }

    /** 我发布的任务 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/myPublish", method = RequestMethod.GET)
    public APIResponse myPublish(String status) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        List<Task> list = service.getMyPublishList(user.getId(), status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 我接的任务 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/myAccept", method = RequestMethod.GET)
    public APIResponse myAccept(String status) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        List<Task> list = service.getMyAcceptList(user.getId(), status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 从请求头 TOKEN 中取当前登录用户 */
    private User currentUser() {
        String token = request.getHeader("TOKEN");
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return userService.getUserByToken(token);
    }

    private List<Long> parseTags(String tags) {
        List<Long> list = new ArrayList<>();
        if (StringUtils.isEmpty(tags)) {
            return list;
        }
        for (String s : tags.split(",")) {
            if (StringUtils.isEmpty(s.trim())) {
                continue;
            }
            try {
                list.add(Long.valueOf(s.trim()));
            } catch (NumberFormatException e) {
                logger.warn("忽略非法的标签值: {}", s);
            }
        }
        return list;
    }

    /** 保存上传的物品图片 */
    public String saveImage(Task task) throws IOException {
        MultipartFile file = task.getImageFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "image" + File.separator + newFileName;
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if (!StringUtils.isEmpty(newFileName)) {
            task.cover = newFileName;
        }
        return newFileName;
    }
}