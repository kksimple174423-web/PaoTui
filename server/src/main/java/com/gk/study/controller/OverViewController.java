package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.management.OperatingSystemMXBean;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Order;
import com.gk.study.entity.Task;
import com.gk.study.entity.User;
import com.gk.study.entity.VisitData;
import com.gk.study.mapper.OrderMapper;
import com.gk.study.mapper.OverviewMapper;
import com.gk.study.mapper.TaskMapper;
import com.gk.study.mapper.UserMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 数据分析：任务量、订单状态分布、用户与骑手规模、近七日访问量
 */
@RestController
@RequestMapping("/overview")
public class OverViewController {

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OverviewMapper overviewMapper;

    @Autowired
    UserMapper userMapper;

    private final static Logger logger = LoggerFactory.getLogger(OverViewController.class);

    @RequestMapping(value = "/sysInfo", method = RequestMethod.GET)
    public APIResponse sysInfo() {

        Map<String, String> map = new HashMap<>();
        map.put("sysName", "校园跑腿任务系统");
        map.put("versionName", "1.0");
        map.put("processor", "2");
        map.put("sysLan", "En");
        map.put("sysZone", "东八区");

        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        map.put("pf", osmxb.getArch());
        map.put("osName", osmxb.getName());
        map.put("cpuCount", String.valueOf(osmxb.getAvailableProcessors()));

        DecimalFormat df = new DecimalFormat("#,##0.0");

        double cpuLoad = osmxb.getSystemCpuLoad();
        double totalvirtualMemory = osmxb.getTotalPhysicalMemorySize();
        double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
        double value = freePhysicalMemorySize / totalvirtualMemory;
        double percentMemoryLoad = ((1 - value) * 100);
        map.put("cpuLoad", df.format(cpuLoad * 100));
        map.put("memory", df.format(totalvirtualMemory / 1024 / 1024 / 1024));
        map.put("usedMemory", df.format((totalvirtualMemory - freePhysicalMemorySize) / 1024 / 1024 / 1024));
        map.put("percentMemory", df.format(percentMemoryLoad));

        map.put("jvmVersion", System.getProperty("java.version"));

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", map);
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public APIResponse count() {

        Map<String, Object> map = new HashMap<>();

        // 任务总数
        QueryWrapper<Task> taskQuery = new QueryWrapper<>();
        long taskTotal = taskMapper.selectCount(taskQuery);
        map.put("spzs", taskTotal);
        map.put("taskTotal", taskTotal);

        // 七日新增任务
        long now = System.currentTimeMillis();
        long sevenMillis = now - 7L * 24 * 60 * 60 * 1000;
        QueryWrapper<Task> weekQuery = new QueryWrapper<>();
        weekQuery.ge("create_time", sevenMillis);
        long weekNew = taskMapper.selectCount(weekQuery);
        map.put("qrxz", weekNew);
        map.put("weekNew", weekNew);

        // 各状态任务数
        map.put("taskWaiting", countTaskByStatus(Task.STATUS_WAITING));     // 待接单
        map.put("taskAccepted", countTaskByStatus(Task.STATUS_ACCEPTED));   // 已接单
        map.put("taskDelivering", countTaskByStatus(Task.STATUS_DELIVERING)); // 配送中
        map.put("taskArrived", countTaskByStatus(Task.STATUS_ARRIVED));     // 已送达
        long finished = countTaskByStatus(Task.STATUS_FINISHED);
        long canceled = countTaskByStatus(Task.STATUS_CANCELED);
        map.put("taskFinished", finished);
        map.put("taskCanceled", canceled);

        // 任务完成率
        String finishRate = "0.0";
        if (taskTotal > 0) {
            finishRate = new DecimalFormat("#,##0.0").format(finished * 100.0 / taskTotal);
        }
        map.put("finishRate", finishRate);

        // 进行中订单（已接单/配送中/已送达）
        QueryWrapper<Order> doingQuery = new QueryWrapper<>();
        doingQuery.in("status", Task.STATUS_ACCEPTED, Task.STATUS_DELIVERING, Task.STATUS_ARRIVED);
        map.put("wfdd", orderMapper.selectCount(doingQuery));
        map.put("doingOrder", map.get("wfdd"));
        map.put("wfddrs", countDistinctPublisher(doingQuery));

        // 已完成订单
        QueryWrapper<Order> doneQuery = new QueryWrapper<>();
        doneQuery.eq("status", Task.STATUS_FINISHED);
        map.put("yfdd", orderMapper.selectCount(doneQuery));
        map.put("doneOrder", map.get("yfdd"));
        map.put("yfddrs", countDistinctPublisher(doneQuery));

        // 已取消订单
        QueryWrapper<Order> cancelQuery = new QueryWrapper<>();
        cancelQuery.eq("status", Task.STATUS_CANCELED);
        map.put("qxdd", orderMapper.selectCount(cancelQuery));
        map.put("cancelOrder", map.get("qxdd"));
        map.put("qxddrs", countDistinctPublisher(cancelQuery));

        // 用户规模
        map.put("userCount", userMapper.selectCount(new QueryWrapper<>()));
        QueryWrapper<User> runnerQuery = new QueryWrapper<>();
        runnerQuery.eq("runner_status", User.RUNNER_PASS);
        map.put("runnerCount", userMapper.selectCount(runnerQuery));

        // 交易总额（已完成任务的赏金合计）
        QueryWrapper<Task> amountQuery = new QueryWrapper<>();
        amountQuery.select("IFNULL(SUM(CAST(reward AS DECIMAL(10,2))),0) as total");
        amountQuery.eq("status", Task.STATUS_FINISHED);
        List<Map<String, Object>> amountList = taskMapper.selectMaps(amountQuery);
        map.put("tradeAmount", amountList.isEmpty() ? "0" : String.valueOf(amountList.get(0).get("total")));

        // 网站流量（近七日）
        List<Object> visitList = new ArrayList<>();
        List<String> sevenList = getSevenDate();
        for (String day : sevenList) {
            Map<String, String> visitMap = new HashMap<>();
            visitMap.put("day", day);
            int pv = 0;
            List<VisitData> webVisitData = overviewMapper.getWebVisitData(day);
            for (VisitData visitData : webVisitData) {
                pv += visitData.count;
            }
            int uv = webVisitData.size();
            visitMap.put("pv", String.valueOf(pv));
            visitMap.put("uv", String.valueOf(uv));
            visitList.add(visitMap);
        }
        map.put("visitList", visitList);

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", map);
    }

    private long countTaskByStatus(String status) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", status);
        return taskMapper.selectCount(queryWrapper);
    }

    private long countDistinctPublisher(QueryWrapper<Order> statusQuery) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        statusQuery.getExpression().getNormal().forEach(queryWrapper.getExpression().getNormal()::add);
        queryWrapper.select("distinct publisher_id");
        return orderMapper.selectList(queryWrapper).size();
    }

    public static List<String> getSevenDate() {

        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < 7; i++) {
            Date date = DateUtils.addDays(new Date(), -i);
            String formatDate = sdf.format(date);
            dateList.add(formatDate);
        }
        Collections.reverse(dateList);
        return dateList;
    }

}