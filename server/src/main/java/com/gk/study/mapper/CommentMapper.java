package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<Comment> getList();

    List<Comment> selectTaskCommentList(@Param("taskId") String taskId, @Param("order") String order);

    List<Comment> selectUserCommentList(@Param("userId") String userId);

    List<Comment> selectReceivedCommentList(@Param("userId") String userId);
}