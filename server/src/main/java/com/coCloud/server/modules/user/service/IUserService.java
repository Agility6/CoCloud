package com.coCloud.server.modules.user.service;

import com.coCloud.server.modules.user.context.*;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coCloud.server.modules.user.vo.UserInfoVO;
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
     *
     * @param userId
     */
    void exit(Long userId);

    /**
     * 用户忘记密码-校验用户名
     *
     * @param checkUsernameContext
     * @return
     */
    String checkUsername(CheckUsernameContext checkUsernameContext);

    /**
     * 用户忘记-校验密保答案
     *
     * @param checkAnswerContext
     * @return
     */
    String checkAnswer(CheckAnswerContext checkAnswerContext);

    /**
     * 重置用户密码
     *
     * @param resetPasswordContext
     */
    void resetPassword(ResetPasswordContext resetPasswordContext);

    /**
     * 在线修改密码
     *
     * @param changePasswordContext
     */
    void changePassword(ChangePasswordContext changePasswordContext);

    /**
     * 查询当前用户的基本信息
     *
     * @param userId
     * @return
     */
    UserInfoVO info(Long userId);
}
