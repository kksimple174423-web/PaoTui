package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Order;
import com.gk.study.service.OrderService;
import com.gk.study.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;

    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    @Override
    public void createOrder(Order order) {
        long ct = System.currentTimeMillis();
        order.setOrderTime(String.valueOf(ct));
        order.setOrderNumber(String.valueOf(ct));
        if (order.getStatus() == null) {
            order.setStatus(Order.STATUS_ACCEPTED);
        }
        mapper.insert(order);
    }

    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateOrder(Order order) {
        mapper.updateById(order);
    }

    @Override
    public List<Order> getUserOrderList(String userId, String runnerId, String status) {
        return mapper.getUserOrderList(userId, runnerId, status);
    }

    @Override
    public Order getOrderByTaskId(String taskId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.orderBy(true, false, "order_time");
        List<Order> list = mapper.selectList(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }
}