package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Order;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * 接单订单接口
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService service;

    /** 全部订单（管理端） */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list() {
        List<Order> list = service.getOrderList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 我的订单：userId=我发布的；runnerId=我接的；status=状态筛选 */
    @RequestMapping(value = "/userOrderList", method = RequestMethod.GET)
    public APIResponse userOrderList(String userId, String runnerId, String status) {
        List<Order> list = service.getUserOrderList(userId, runnerId, status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 按任务查询订单详情 */
    @RequestMapping(value = "/detailByTask", method = RequestMethod.GET)
    public APIResponse detailByTask(String taskId) {
        Order order = service.getOrderByTaskId(taskId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", order);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Order order) throws IOException {
        service.createOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids) {
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteOrder(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Order order) throws IOException {
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /** 用户取消订单 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelUserOrder(Long id) throws IOException {
        Order order = new Order();
        order.setId(id);
        order.setStatus(Order.STATUS_CANCELED);
        order.setCancelReason("用户取消");
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

    /** 管理员取消订单 */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelOrder(Long id) throws IOException {
        Order order = new Order();
        order.setId(id);
        order.setStatus(Order.STATUS_CANCELED);
        order.setCancelReason("管理员取消");
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

}