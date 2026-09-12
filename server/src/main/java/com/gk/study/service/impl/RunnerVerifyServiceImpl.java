package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.RunnerVerify;
import com.gk.study.entity.User;
import com.gk.study.mapper.RunnerVerifyMapper;
import com.gk.study.mapper.UserMapper;
import com.gk.study.service.RunnerVerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RunnerVerifyServiceImpl extends ServiceImpl<RunnerVerifyMapper, RunnerVerify> implements RunnerVerifyService {

    @Autowired
    RunnerVerifyMapper mapper;

    @Autowired
    UserMapper userMapper;

    @Override
    @Transactional
    public void apply(RunnerVerify verify) {
        verify.setStatus(RunnerVerify.STATUS_WAITING);
        verify.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(verify);

        // 用户骑手状态置为待审核
        User user = userMapper.selectById(verify.getUserId());
        if (user != null) {
            user.setRunnerStatus(User.RUNNER_AUDITING);
            userMapper.updateById(user);
        }
    }

    @Override
    public RunnerVerify getMyApply(String userId) {
        QueryWrapper<RunnerVerify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderBy(true, false, "create_time");
        List<RunnerVerify> list = mapper.selectList(queryWrapper);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<RunnerVerify> getList(String status) {
        QueryWrapper<RunnerVerify> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderBy(true, false, "create_time");
        List<RunnerVerify> list = mapper.selectList(queryWrapper);
        for (RunnerVerify verify : list) {
            User user = userMapper.selectById(verify.getUserId());
            if (user != null) {
                verify.setUsername(user.getUsername());
                verify.setNickname(user.getNickname());
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void audit(String id, String status, String auditRemark) {
        RunnerVerify verify = mapper.selectById(id);
        if (verify == null) {
            return;
        }
        verify.setStatus(status);
        verify.setAuditRemark(auditRemark);
        verify.setAuditTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(verify);

        // 同步用户的骑手认证状态
        User user = userMapper.selectById(verify.getUserId());
        if (user != null) {
            if (RunnerVerify.STATUS_PASS.equals(status)) {
                user.setRunnerStatus(User.RUNNER_PASS);
            } else if (RunnerVerify.STATUS_REJECT.equals(status)) {
                user.setRunnerStatus(User.RUNNER_REJECT);
            }
            userMapper.updateById(user);
        }
    }
}