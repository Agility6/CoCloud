package com.coCloud.server.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * ClassName: CheckUsernamePO
 * Description: 校验用户名称Context对象
 *
 * @Author agility6
 * @Create 2024/5/15 20:55
 * @Version: 1.0
 */
@Data
public class CheckUsernameContext implements Serializable {

    private static final long serialVersionUID = 9078863427981517496L;

    /**
     * 用户名称
     */
    private String username;
}
