package com.coCloud.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: ChangePasswordPO
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/15 23:30
 * @Version: 1.0
 */
@Data
@ApiModel(value = "用户在线修改密码参数")
public class ChangePasswordPO implements Serializable {

    private static final long serialVersionUID = 7939884324524415657L;

    @ApiModelProperty(value = "旧密码", required = true)
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 8, max = 16, message ="请输入8-16位的旧密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Length(min = 8, max = 16, message ="请输入8-16位的新密码")
    private String newPassword;

}
