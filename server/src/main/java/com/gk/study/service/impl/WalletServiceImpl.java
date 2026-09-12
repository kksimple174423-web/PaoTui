package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.User;
import com.gk.study.entity.Wallet;
import com.gk.study.entity.WalletRecord;
import com.gk.study.entity.Withdraw;
import com.gk.study.mapper.UserMapper;
import com.gk.study.mapper.WalletMapper;
import com.gk.study.mapper.WalletRecordMapper;
import com.gk.study.mapper.WithdrawMapper;
import com.gk.study.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 钱包实现：充值、赏金托管、自动结算、提现
 */
@Service
public class WalletServiceImpl extends ServiceImpl<WalletMapper, Wallet> implements WalletService {

    @Autowired
    WalletMapper mapper;

    @Autowired
    WalletRecordMapper recordMapper;

    @Autowired
    WithdrawMapper withdrawMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public Wallet getWallet(String userId) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        Wallet wallet = mapper.selectOne(queryWrapper);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalance("0.00");
            wallet.setFrozen("0.00");
            wallet.setTotalIncome("0.00");
            wallet.setTotalExpense("0.00");
            wallet.setCreateTime(String.valueOf(System.currentTimeMillis()));
            wallet.setUpdateTime(wallet.getCreateTime());
            mapper.insert(wallet);
        }
        return wallet;
    }

    @Override
    public List<WalletRecord> getRecords(String userId, String type) {
        QueryWrapper<WalletRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (type != null && !type.isEmpty()) {
            queryWrapper.eq("type", type);
        }
        queryWrapper.orderBy(true, false, "create_time");
        List<WalletRecord> list = recordMapper.selectList(queryWrapper);
        for (WalletRecord record : list) {
            record.setTypeText(typeText(record.getType()));
        }
        return list;
    }

    @Override
    @Transactional
    public void recharge(String userId, String amount, String remark) {
        BigDecimal money = toDecimal(amount);
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        Wallet wallet = getWallet(userId);
        wallet.setBalance(toStr(toDecimal(wallet.getBalance()).add(money)));
        wallet.setTotalIncome(toStr(toDecimal(wallet.getTotalIncome()).add(money)));
        wallet.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(wallet);
        saveRecord(userId, WalletRecord.TYPE_RECHARGE, money, wallet.getBalance(), null, null,
                remark == null ? "钱包充值" : remark);
    }

    @Override
    @Transactional
    public boolean freeze(String userId, String amount, String taskId) {
        BigDecimal money = toDecimal(amount);
        Wallet wallet = getWallet(userId);
        BigDecimal balance = toDecimal(wallet.getBalance());
        if (balance.compareTo(money) < 0) {
            return false;
        }
        wallet.setBalance(toStr(balance.subtract(money)));
        wallet.setFrozen(toStr(toDecimal(wallet.getFrozen()).add(money)));
        wallet.setTotalExpense(toStr(toDecimal(wallet.getTotalExpense()).add(money)));
        wallet.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(wallet);
        saveRecord(userId, WalletRecord.TYPE_TASK_PAY, money.negate(), wallet.getBalance(), taskId, null, "发布任务托管赏金");
        return true;
    }

    @Override
    @Transactional
    public void settle(String publisherId, String runnerId, String amount, String taskId, String orderId) {
        BigDecimal money = toDecimal(amount);

        // 发布者：解冻（资金已经作为支出托管）
        if (publisherId != null) {
            Wallet publisher = getWallet(publisherId);
            publisher.setFrozen(toStr(toDecimal(publisher.getFrozen()).subtract(money)));
            publisher.setUpdateTime(String.valueOf(System.currentTimeMillis()));
            mapper.updateById(publisher);
        }

        // 骑手：赏金入账
        if (runnerId != null) {
            Wallet runner = getWallet(runnerId);
            runner.setBalance(toStr(toDecimal(runner.getBalance()).add(money)));
            runner.setTotalIncome(toStr(toDecimal(runner.getTotalIncome()).add(money)));
            runner.setUpdateTime(String.valueOf(System.currentTimeMillis()));
            mapper.updateById(runner);
            saveRecord(runnerId, WalletRecord.TYPE_TASK_INCOME, money, runner.getBalance(), taskId, orderId, "完成任务获得赏金");
        }
    }

    @Override
    @Transactional
    public void refund(String userId, String amount, String taskId) {
        BigDecimal money = toDecimal(amount);
        Wallet wallet = getWallet(userId);
        wallet.setFrozen(toStr(toDecimal(wallet.getFrozen()).subtract(money)));
        wallet.setBalance(toStr(toDecimal(wallet.getBalance()).add(money)));
        wallet.setTotalExpense(toStr(toDecimal(wallet.getTotalExpense()).subtract(money)));
        wallet.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(wallet);
        saveRecord(userId, WalletRecord.TYPE_REFUND, money, wallet.getBalance(), taskId, null, "任务取消，托管赏金退回");
    }

    @Override
    @Transactional
    public void applyWithdraw(Withdraw withdraw) {
        BigDecimal money = toDecimal(withdraw.getAmount());
        Wallet wallet = getWallet(withdraw.getUserId());
        if (toDecimal(wallet.getBalance()).compareTo(money) < 0) {
            return;
        }
        // 先冻结，等管理员打款后再扣除
        wallet.setBalance(toStr(toDecimal(wallet.getBalance()).subtract(money)));
        wallet.setFrozen(toStr(toDecimal(wallet.getFrozen()).add(money)));
        wallet.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(wallet);

        withdraw.setStatus(Withdraw.STATUS_WAITING);
        withdraw.setApplyTime(String.valueOf(System.currentTimeMillis()));
        withdrawMapper.insert(withdraw);

        saveRecord(withdraw.getUserId(), WalletRecord.TYPE_WITHDRAW, money.negate(), wallet.getBalance(), null, null, "提现申请");
    }

    @Override
    public List<Withdraw> getWithdrawList(String userId, String status) {
        QueryWrapper<Withdraw> queryWrapper = new QueryWrapper<>();
        if (userId != null && !userId.isEmpty()) {
            queryWrapper.eq("user_id", userId);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderBy(true, false, "apply_time");
        List<Withdraw> list = withdrawMapper.selectList(queryWrapper);
        for (Withdraw withdraw : list) {
            User user = userMapper.selectById(withdraw.getUserId());
            if (user != null) {
                withdraw.setUsername(user.getUsername());
                withdraw.setNickname(user.getNickname());
            }
        }
        return list;
    }

    @Override
    @Transactional
    public void auditWithdraw(String id, String status, String handleRemark) {
        Withdraw withdraw = withdrawMapper.selectById(id);
        if (withdraw == null) {
            return;
        }
        withdraw.setStatus(status);
        withdraw.setHandleRemark(handleRemark);
        withdraw.setHandleTime(String.valueOf(System.currentTimeMillis()));
        withdrawMapper.updateById(withdraw);

        BigDecimal money = toDecimal(withdraw.getAmount());
        Wallet wallet = getWallet(withdraw.getUserId());
        if (Withdraw.STATUS_REJECT.equals(status)) {
            // 驳回：冻结金额退回可用余额
            wallet.setFrozen(toStr(toDecimal(wallet.getFrozen()).subtract(money)));
            wallet.setBalance(toStr(toDecimal(wallet.getBalance()).add(money)));
            saveRecord(withdraw.getUserId(), WalletRecord.TYPE_REFUND, money, wallet.getBalance(), null, null, "提现被驳回，金额退回");
        } else if (Withdraw.STATUS_PASS.equals(status)) {
            // 打款：扣除冻结金额
            wallet.setFrozen(toStr(toDecimal(wallet.getFrozen()).subtract(money)));
            wallet.setTotalExpense(toStr(toDecimal(wallet.getTotalExpense()).add(money)));
        }
        wallet.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(wallet);
    }

    private void saveRecord(String userId, String type, BigDecimal amount, String balanceAfter,
                            String taskId, String orderId, String remark) {
        WalletRecord record = new WalletRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setAmount(toStr(amount));
        record.setBalanceAfter(balanceAfter);
        record.setTaskId(taskId);
        record.setOrderId(orderId);
        record.setRemark(remark);
        record.setCreateTime(String.valueOf(System.currentTimeMillis()));
        recordMapper.insert(record);
    }

    private String typeText(String type) {
        if (WalletRecord.TYPE_RECHARGE.equals(type)) return "充值";
        if (WalletRecord.TYPE_TASK_PAY.equals(type)) return "发布任务托管";
        if (WalletRecord.TYPE_TASK_INCOME.equals(type)) return "任务收入";
        if (WalletRecord.TYPE_REFUND.equals(type)) return "退款";
        if (WalletRecord.TYPE_WITHDRAW.equals(type)) return "提现";
        if (WalletRecord.TYPE_BONUS.equals(type)) return "平台奖励";
        return "其他";
    }

    private BigDecimal toDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String toStr(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}