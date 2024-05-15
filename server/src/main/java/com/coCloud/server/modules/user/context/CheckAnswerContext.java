package com.coCloud.server.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * ClassName: CheckAnswerPO
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/15 21:07
 * @Version: 1.0
 */
@Data
public class CheckAnswerContext implements Serializable {

    private static final long serialVersionUID = -3217055050519676527L;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;
}
