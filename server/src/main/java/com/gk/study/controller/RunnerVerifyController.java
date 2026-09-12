package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.RunnerVerify;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.RunnerVerifyService;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 骑手认证接口：用户提交认证资料，管理员审核
 */
@RestController
@RequestMapping("/runnerVerify")
public class RunnerVerifyController {

    private final static Logger logger = LoggerFactory.getLogger(RunnerVerifyController.class);

    @Autowired
    RunnerVerifyService service;

    @Autowired
    UserService userService;

    @Autowired
    HttpServletRequest request;

    @Value("${File.uploadPath}")
    private String uploadPath;

    /** 提交骑手认证申请 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @Transactional
    public APIResponse apply(RunnerVerify verify) throws IOException {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        if (User.RUNNER_PASS.equals(user.getRunnerStatus())) {
            return new APIResponse(ResponeCode.FAIL, "你已经通过骑手认证了");
        }
        RunnerVerify exist = service.getMyApply(user.getId());
        if (exist != null && RunnerVerify.STATUS_WAITING.equals(exist.getStatus())) {
            return new APIResponse(ResponeCode.FAIL, "认证申请正在审核中，请耐心等待");
        }

        verify.setUserId(user.getId());
        String image = saveCardImage(verify);
        if (!StringUtils.isEmpty(image)) {
            verify.setCardImage(image);
        }
        service.apply(verify);
        return new APIResponse(ResponeCode.SUCCESS, "认证申请已提交，请等待管理员审核");
    }

    /** 查询我的认证申请 */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/myApply", method = RequestMethod.GET)
    public APIResponse myApply() {
        User user = currentUser();
        if (user == null) {
            return new APIResponse(ResponeCode.FAIL, "未登录");
        }
        RunnerVerify verify = service.getMyApply(user.getId());
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", verify);
    }

    /** 认证申请列表（管理端） */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String status) {
        List<RunnerVerify> list = service.getList(status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    /** 审核认证申请（管理端） */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    @Transactional
    public APIResponse audit(String id, String status, String auditRemark) {
        service.audit(id, status, auditRemark);
        return new APIResponse(ResponeCode.SUCCESS, "审核完成");
    }

    private User currentUser() {
        String token = request.getHeader("TOKEN");
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        return userService.getUserByToken(token);
    }

    /** 保存证件照 */
    public String saveCardImage(RunnerVerify verify) throws IOException {
        MultipartFile file = verify.getCardFile();
        String newFileName = null;
        if (file != null && !file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "verify" + File.separator + newFileName;
            File destFile = new File(filePath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        return newFileName;
    }
}