package com.coCloud.server.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * ClassName: ResetPasswordPO
 * Description: 重置用户密码Context对象
 *
 * @Author agility6
 * @Create 2024/5/15 21:19
 * @Version: 1.0
 */
@Data
public class ResetPasswordContext implements Serializable {

    private static final long serialVersionUID = -971533778693623667L;

    private String username;

    private String password;

    private String token;
}
