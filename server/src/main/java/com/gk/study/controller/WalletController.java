package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.User;
import com.gk.study.entity.Wallet;
import com.gk.study.entity.WalletRecord;
import com.gk.study.entity.Withdraw;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import com.gk.study.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包接口：余额、充值、明细、提现申请与审核
 */
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final static Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    WalletService service;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;

    /** 我的钱包 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/my", method = RequestMethod.GET)
    public APIResponse my() {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        Wallet wallet = service.getWallet(user.getId());
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", wallet);
    }

    /** 资金明细 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/records", method = RequestMethod.GET)
    public APIResponse records(String type) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        List<WalletRecord> list = service.getRecords(user.getId(), type);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 充值（演示环境直接到账） */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/recharge", method = RequestMethod.POST)
    @Transactional
    public APIResponse recharge(String amount) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        if (!StringUtils.hasText(amount) || new BigDecimal(amount).compareTo(BigDecimal.ZERO) <= 0) {
            return new APIResponse(ResponeCode.FAIL, "请输入正确的充值金额");
        }
        service.recharge(user.getId(), amount, "钱包充值");
        return new APIResponse(ResponeCode.SUCCESS, "充值成功");
    }

    /** 申请提现 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/withdraw", method = RequestMethod.POST)
    @Transactional
    public APIResponse withdraw(Withdraw withdraw) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        if (!StringUtils.hasText(withdraw.getAmount())
                || new BigDecimal(withdraw.getAmount()).compareTo(BigDecimal.ZERO) <= 0) {
            return new APIResponse(ResponeCode.FAIL, "请输入正确的提现金额");
        }
        Wallet wallet = service.getWallet(user.getId());
        if (new BigDecimal(wallet.getBalance()).compareTo(new BigDecimal(withdraw.getAmount())) < 0) {
            return new APIResponse(ResponeCode.FAIL, "可用余额不足");
        }
        withdraw.setUserId(user.getId());
        if (!StringUtils.hasText(withdraw.getRealName())) {
            withdraw.setRealName(user.getRealName());
        }
        service.applyWithdraw(withdraw);
        return new APIResponse(ResponeCode.SUCCESS, "提现申请已提交");
    }

    /** 我的提现记录 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/withdrawList", method = RequestMethod.GET)
    public APIResponse withdrawList(String status) {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        List<Withdraw> list = service.getWithdrawList(user.getId(), status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 全部提现申请（管理端） */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/allWithdraw", method = RequestMethod.GET)
    public APIResponse allWithdraw(String status) {
        List<Withdraw> list = service.getWithdrawList(null, status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 提现审核（管理端）：status=1 已打款 / 2 已驳回 */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/auditWithdraw", method = RequestMethod.POST)
    @Transactional
    public APIResponse auditWithdraw(String id, String status, String handleRemark) {
        service.auditWithdraw(id, status, handleRemark);
        return new APIResponse(ResponeCode.SUCCESS, "处理完成");
    }

    private User currentUser() {
        String token = request.getHeader("TOKEN");
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return userService.getUserByToken(token);
    }
}