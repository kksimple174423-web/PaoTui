package com.gk.study.service;

import com.gk.study.entity.Wallet;
import com.gk.study.entity.WalletRecord;
import com.gk.study.entity.Withdraw;

import java.util.List;

/**
 * 钱包与赏金托管业务接口
 */
public interface WalletService {

    /** 获取钱包（不存在则自动创建） */
    Wallet getWallet(String userId);

    /** 资金明细 */
    List<WalletRecord> getRecords(String userId, String type);

    /** 充值 */
    void recharge(String userId, String amount, String remark);

    /** 发布任务：赏金托管（可用余额 -> 冻结） */
    boolean freeze(String userId, String amount, String taskId);

    /** 任务完成：发布者冻结金额划转给骑手 */
    void settle(String publisherId, String runnerId, String amount, String taskId, String orderId);

    /** 任务取消：托管赏金退回发布者 */
    void refund(String userId, String amount, String taskId);

    /** 提交提现申请 */
    void applyWithdraw(Withdraw withdraw);

    /** 提现记录（管理端可查全部） */
    List<Withdraw> getWithdrawList(String userId, String status);

    /** 管理端审核提现 */
    void auditWithdraw(String id, String status, String handleRemark);
}