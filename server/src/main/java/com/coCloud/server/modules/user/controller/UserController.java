package com.coCloud.server.modules.user.controller;

import com.coCloud.core.response.R;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.annotation.LoginIgnore;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.user.context.UserLoginContext;
import com.coCloud.server.modules.user.context.UserRegisterContext;
import com.coCloud.server.modules.user.converter.UserConverter;
import com.coCloud.server.modules.user.po.UserLoginPO;
import com.coCloud.server.modules.user.po.UserRegisterPO;
import com.coCloud.server.modules.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserController
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/13 10:55
 * @Version: 1.0
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private UserConverter userConverter;

    @ApiOperation(value = "用户注册接口", notes = "该接口提供了用户注册的功能，实现了冥等性注册的逻辑，可以放心多并发调用", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @LoginIgnore
    @PostMapping("/register")
    public R register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext userRegisterContext = userConverter.userRegisterPO2UserRegisterContext(userRegisterPO);
        Long userId = iUserService.register(userRegisterContext);
        // 加密
        return R.data(IdUtil.encrypt(userId));
    }

    @ApiOperation(value = "用户登录接口", notes = "该接口提供了用户登录的功能，成功登陆之后，会返回有时效性的accessToken供后续服务使用", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @LoginIgnore
    @PostMapping("/login")
    public R login(@Validated @RequestBody UserLoginPO userLoginPo) {
        UserLoginContext userLoginContext = userConverter.userLoginPO2UserLoginContext(userLoginPo);
        String accessToken = iUserService.login(userLoginContext);
        return R.data(accessToken);
    }

    @ApiOperation(
            value = "用户登出接口",
            notes = "该接口提供了用户登出的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("/exit")
    public R exit() {
        iUserService.exit(UserIdUtil.get());
        return R.success();
    }
}
