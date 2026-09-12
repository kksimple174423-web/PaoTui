package com.gk.study.service;

import com.gk.study.entity.Order;

import java.util.List;

/**
 * 接单订单业务接口
 */
public interface OrderService {

    List<Order> getOrderList();

    void createOrder(Order order);

    void deleteOrder(String id);

    void updateOrder(Order order);

    /** 我的订单：userId 查我发布的，runnerId 查我接的 */
    List<Order> getUserOrderList(String userId, String runnerId, String status);

    /** 按任务查询订单 */
    Order getOrderByTaskId(String taskId);
}