package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Classification;
import com.gk.study.entity.Order;
import com.gk.study.entity.Task;
import com.gk.study.entity.TaskTag;
import com.gk.study.entity.User;
import com.gk.study.mapper.ClassificationMapper;
import com.gk.study.mapper.OrderMapper;
import com.gk.study.mapper.TaskMapper;
import com.gk.study.mapper.TaskTagMapper;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.TaskService;
import com.gk.study.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跑腿任务业务实现
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    TaskMapper mapper;

    @Autowired
    TaskTagMapper taskTagMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ClassificationMapper classificationMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    WalletService walletService;

    @Override
    public List<Task> getTaskList(String keyword, String sort, String c, String tag, String status) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();

        // 关键词搜索（任务标题）
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 按任务状态筛选
        if (StringUtils.isNotBlank(status) && !status.equals("-1")) {
            queryWrapper.eq("status", status);
        }

        // 按任务类型筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq("classification_id", c);
        }

        // 排序：recent最新 / hot最热 / reward赏金从高到低
        if (StringUtils.isNotBlank(sort) && sort.equals("reward")) {
            queryWrapper.orderBy(true, false, "reward");
        } else if (StringUtils.isNotBlank(sort) && (sort.equals("hot") || sort.equals("recommend"))) {
            queryWrapper.orderBy(true, false, "pv");
        } else {
            queryWrapper.orderBy(true, false, "create_time");
        }

        List<Task> tasks = mapper.selectList(queryWrapper);

        // 按标签筛选
        if (StringUtils.isNotBlank(tag)) {
            QueryWrapper<TaskTag> tagQuery = new QueryWrapper<>();
            tagQuery.eq("tag_id", tag);
            List<Long> taskIds = taskTagMapper.selectList(tagQuery)
                    .stream().map(TaskTag::getTaskId).collect(Collectors.toList());
            tasks = tasks.stream().filter(t -> taskIds.contains(t.getId())).collect(Collectors.toList());
        }

        fillTaskDetail(tasks);
        return tasks;
    }

    /** 任务大厅：只展示还可以接单的任务 */
    @Override
    public List<Task> getHallList(String keyword, String c, String sort) {
        return getTaskList(keyword, sort, c, null, Task.STATUS_WAITING);
    }

    @Override
    public Task getTaskById(String id) {
        Task task = mapper.selectById(id);
        if (task != null) {
            fillTaskDetail(java.util.Collections.singletonList(task));
        }
        return task;
    }

    @Override
    @Transactional
    public void createTask(Task task) {
        task.setCreateTime(String.valueOf(System.currentTimeMillis()));
        if (task.getStatus() == null) {
            task.setStatus(Task.STATUS_WAITING);
        }
        if (task.getPv() == null) {
            task.setPv("0");
        }
        if (task.getWishCount() == null) {
            task.setWishCount("0");
        }
        if (task.getCollectCount() == null) {
            task.setCollectCount("0");
        }
        if (task.getRecommendCount() == null) {
            task.setRecommendCount("0");
        }
        task.setOrderCount("0");
        mapper.insert(task);
        setTaskTags(task);
    }

    @Override
    public void updateTask(Task task) {
        setTaskTags(task);
        mapper.updateById(task);
    }

    @Override
    public void deleteTask(String id) {
        mapper.deleteById(id);
    }

    /** 骑手接单：更新任务状态并生成接单订单 */
    @Override
    @Transactional
    public Task acceptTask(String taskId, String runnerId) {
        Task task = mapper.selectById(taskId);
        if (task == null) {
            return null;
        }
        // 只有待接单的任务可以接
        if (!Task.STATUS_WAITING.equals(task.getStatus())) {
            return null;
        }
        // 不能接自己发布的任务
        if (task.getPublisherId() != null && String.valueOf(task.getPublisherId()).equals(runnerId)) {
            return null;
        }

        task.setRunnerId(Long.valueOf(runnerId));
        task.setStatus(Task.STATUS_ACCEPTED);
        task.setOrderCount(String.valueOf(parseInt(task.getOrderCount(), 0) + 1));
        mapper.updateById(task);

        // 生成接单订单
        long ct = System.currentTimeMillis();
        Order order = new Order();
        order.setTaskId(String.valueOf(task.getId()));
        order.setPublisherId(String.valueOf(task.getPublisherId()));
        order.setRunnerId(runnerId);
        order.setAmount(task.getReward());
        order.setStatus(Task.STATUS_ACCEPTED);
        order.setOrderNumber(String.valueOf(ct));
        order.setOrderTime(String.valueOf(ct));
        order.setAcceptTime(String.valueOf(ct));
        order.setReceiverName(task.getPublisherName());
        order.setReceiverAddress(task.getDeliveryAddress());
        order.setReceiverPhone(task.getContactPhone());
        orderMapper.insert(order);

        return getTaskById(taskId);
    }

    /** 任务状态流转，同时同步订单状态 */
    @Override
    @Transactional
    public void updateStatus(String taskId, String status) {
        Task task = mapper.selectById(taskId);
        if (task == null) {
            return;
        }
        task.setStatus(status);
        if (Task.STATUS_FINISHED.equals(status)) {
            task.setFinishTime(String.valueOf(System.currentTimeMillis()));
        }
        mapper.updateById(task);

        // 同步订单
        QueryWrapper<Order> orderQuery = new QueryWrapper<>();
        orderQuery.eq("task_id", taskId);
        List<Order> orders = orderMapper.selectList(orderQuery);
        for (Order order : orders) {
            order.setStatus(status);
            long ct = System.currentTimeMillis();
            if (Task.STATUS_DELIVERING.equals(status)) {
                order.setStatusRemark("骑手已取件，配送中");
            } else if (Task.STATUS_ARRIVED.equals(status)) {
                order.setFinishTime(String.valueOf(ct));
                order.setStatusRemark("骑手已送达");
            } else if (Task.STATUS_FINISHED.equals(status)) {
                order.setConfirmTime(String.valueOf(ct));
                order.setStatusRemark("用户已确认完成");
            } else if (Task.STATUS_CANCELED.equals(status)) {
                order.setCancelReason("任务取消");
            }
            orderMapper.updateById(order);
        }

        // 钱包结算：任务完成 -> 托管赏金划给骑手；任务取消 -> 托管赏金退回发布者
        if (Task.STATUS_FINISHED.equals(status) && !orders.isEmpty()) {
            walletService.settle(String.valueOf(task.getPublisherId()), String.valueOf(task.getRunnerId()),
                    task.getReward(), taskId, String.valueOf(orders.get(0).getId()));
        } else if (Task.STATUS_CANCELED.equals(status)) {
            walletService.refund(String.valueOf(task.getPublisherId()), task.getReward(), taskId);
        }

        // 任务完成：骑手完成单数 +1，并把赏金结算进累计收益
        if (Task.STATUS_FINISHED.equals(status) && task.getRunnerId() != null) {
            User runner = userMapper.selectById(task.getRunnerId());
            if (runner != null) {
                runner.setFinishCount(String.valueOf(parseInt(runner.getFinishCount(), 0) + 1));

                BigDecimal income = new BigDecimal(runner.getTotalIncome() == null ? "0" : runner.getTotalIncome());
                BigDecimal reward = new BigDecimal(task.getReward() == null ? "0" : task.getReward());
                income = income.add(reward).setScale(2, RoundingMode.HALF_UP);
                runner.setTotalIncome(income.toString());

                userMapper.updateById(runner);
            }
        }
    }

    @Override
    public List<Task> getMyPublishList(String userId, String status) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("publisher_id", userId);
        if (StringUtils.isNotBlank(status) && !status.equals("-1")) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderBy(true, false, "create_time");
        List<Task> tasks = mapper.selectList(queryWrapper);
        fillTaskDetail(tasks);
        return tasks;
    }

    @Override
    public List<Task> getMyAcceptList(String runnerId, String status) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("runner_id", runnerId);
        if (StringUtils.isNotBlank(status) && !status.equals("-1")) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderBy(true, false, "create_time");
        List<Task> tasks = mapper.selectList(queryWrapper);
        fillTaskDetail(tasks);
        return tasks;
    }

    @Override
    public void addWishCount(String taskId) {
        Task task = mapper.selectById(taskId);
        if (task != null) {
            task.setWishCount(String.valueOf(parseInt(task.getWishCount(), 0) + 1));
            mapper.updateById(task);
        }
    }

    @Override
    public void addCollectCount(String taskId) {
        Task task = mapper.selectById(taskId);
        if (task != null) {
            task.setCollectCount(String.valueOf(parseInt(task.getCollectCount(), 0) + 1));
            mapper.updateById(task);
        }
    }

    @Override
    public void addPv(String taskId) {
        Task task = mapper.selectById(taskId);
        if (task != null) {
            task.setPv(String.valueOf(parseInt(task.getPv(), 0) + 1));
            mapper.updateById(task);
        }
    }

    /** 补充任务的标签、发布者、骑手、类型等展示信息 */
    public void fillTaskDetail(List<Task> tasks) {
        for (Task task : tasks) {
            // 标签
            QueryWrapper<TaskTag> tagQuery = new QueryWrapper<>();
            tagQuery.lambda().eq(TaskTag::getTaskId, task.getId());
            List<Long> tags = taskTagMapper.selectList(tagQuery)
                    .stream().map(TaskTag::getTagId).collect(Collectors.toList());
            task.setTags(tags);

            // 发布者信息
            if (task.getPublisherId() != null) {
                User publisher = userMapper.selectById(task.getPublisherId());
                if (publisher != null) {
                    task.setPublisherName(StringUtils.isNotBlank(publisher.getNickname())
                            ? publisher.getNickname() : publisher.getUsername());
                    task.setPublisherAvatar(publisher.getAvatar());
                    task.setPublisherCredit(publisher.getCreditScore());
                }
            }

            // 骑手信息
            if (task.getRunnerId() != null) {
                User runner = userMapper.selectById(task.getRunnerId());
                if (runner != null) {
                    task.setRunnerName(StringUtils.isNotBlank(runner.getNickname())
                            ? runner.getNickname() : runner.getUsername());
                    task.setRunnerAvatar(runner.getAvatar());
                }
            }

            // 任务类型名称
            if (task.getClassificationId() != null) {
                Classification classification = classificationMapper.selectById(task.getClassificationId());
                if (classification != null) {
                    task.setClassificationTitle(classification.getTitle());
                }
            }
        }
    }

    /** 维护任务标签关联 */
    public void setTaskTags(Task task) {
        Map<String, Object> map = new HashMap<>();
        map.put("task_id", task.getId());
        taskTagMapper.deleteByMap(map);
        if (task.getTags() != null) {
            for (Long tag : task.getTags()) {
                TaskTag taskTag = new TaskTag();
                taskTag.setTaskId(task.getId());
                taskTag.setTagId(tag);
                taskTagMapper.insert(taskTag);
            }
        }
    }

    private int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}