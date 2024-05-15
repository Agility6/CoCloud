package com.coCloud.server.modules.user.context;

import com.coCloud.server.modules.user.entity.CoCloudUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: ChangePasswordPO
 * Description: 用户在线修改密码上下文信息实体
 *
 * @Author agility6
 * @Create 2024/5/15 23:30
 * @Version: 1.0
 */
@Data
public class ChangePasswordContext implements Serializable {

    private static final long serialVersionUID = -8951848923698107528L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 当前登录用户的实体信息
     */
    private CoCloudUser entity;
}
