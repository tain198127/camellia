package com.netease.nim.camellia.console.controller;

import com.netease.nim.camellia.console.conf.ConsoleProperties;
import com.netease.nim.camellia.console.model.WebResult;
import com.netease.nim.camellia.console.service.UserAccessService;
import com.netease.nim.camellia.console.service.ao.UserLoginAO;
import com.netease.nim.camellia.console.service.vo.UserLoginVO;
import com.netease.nim.camellia.console.util.LogBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Create with IntelliJ IDEA
 *
 * @author ChenHongliang
 */
@Api(value = "登录接口", tags = {"LoginController"})
@RestController
@ConditionalOnClass(ConsoleProperties.class)
@RequestMapping(value = "/camellia/console/login")
public class LoginController {

    @Autowired
    UserAccessService userAccessService;

    @ApiOperation(value = "登录接口", notes = "用户名和密码，返回登录信息")
    @PostMapping
    public WebResult login(@RequestBody UserLoginAO loginAO){
        LogBean.get().addProps("loginAO",loginAO);
        UserLoginVO login;
        try {
            login= userAccessService.login(loginAO);
            LogBean.get().addProps("ret",login);
            return WebResult.success(login);
        }catch (Exception e){
            LogBean.get().addProps("ret","false");
            return WebResult.Exception(e);
        }
    }
}
