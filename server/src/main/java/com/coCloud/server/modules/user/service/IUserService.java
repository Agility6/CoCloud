package com.coCloud.server.modules.user.service;

import com.coCloud.server.modules.user.context.UserLoginContext;
import com.coCloud.server.modules.user.context.UserRegisterContext;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * @author agility6
 * @description 针对表【co_cloud_user(用户信息表)】的数据库操作Service
 * @createDate 2024-05-10 19:20:36
 */
public interface IUserService extends IService<CoCloudUser> {

    /**
     * 用户注册业务
     *
     * @param userRegisterContext
     * @return
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录业务
     *
     * @param userLoginContext
     * @return
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 用户退出登录
     * @param userId
     */
    void exit(Long userId);
}
