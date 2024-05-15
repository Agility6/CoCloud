package com.coCloud.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * ClassName: CheckUsernamePO
 * Description: 校验用户名称PO对象
 *
 * @Author agility6
 * @Create 2024/5/15 20:55
 * @Version: 1.0
 */
@ApiModel(value = "用户忘记密码-校验用户参数")
@Data
public class CheckUsernamePO implements Serializable {

    private static final long serialVersionUID = -4298572312341712337L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;
}
