package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getList();

    List<Order> getUserOrderList(@Param("userId") String userId,
                                 @Param("runnerId") String runnerId,
                                 @Param("status") String status);
}