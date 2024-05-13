package com.coCloud.server.modules.user.context;

import com.coCloud.server.modules.user.entity.CoCloudUser;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserRegisterContext
 * Description: 用户注册业务的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/5/13 11:02
 * @Version: 1.0
 */
@Data
public class UserRegisterContext implements Serializable {

    private static final long serialVersionUID = -673919143659644655L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;

    /**
     * 用户实体对象
     */
    private CoCloudUser entity;

}
