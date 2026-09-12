package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.TaskCollect;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskCollectMapper extends BaseMapper<TaskCollect> {


    List<Map> getTaskCollectList(String userId);
}
