package com.gk.study.service;

import com.gk.study.entity.RunnerVerify;

import java.util.List;

/**
 * 骑手认证业务接口
 */
public interface RunnerVerifyService {

    /** 提交认证申请（同时把用户的骑手状态置为待审核） */
    void apply(RunnerVerify verify);

    /** 查询我最新的一条申请 */
    RunnerVerify getMyApply(String userId);

    /** 认证申请列表（管理端） */
    List<RunnerVerify> getList(String status);

    /** 审核：status=1 通过 / 2 驳回 */
    void audit(String id, String status, String auditRemark);
}