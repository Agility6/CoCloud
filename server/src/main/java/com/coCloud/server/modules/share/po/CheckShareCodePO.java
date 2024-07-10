package com.coCloud.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: CheckShareCodePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/10 14:13
 * @Version: 1.0
 */
@ApiModel("校验分享码参数实体对象")
@Data
public class CheckShareCodePO implements Serializable {

    private static final long serialVersionUID = -2919458312659186837L;

    @ApiModelProperty(value = "分享的ID", required = true)
    @NotBlank(message = "分享ID不能为空")
    private String shareId;

    @ApiModelProperty(value = "分享的分享码", required = true)
    @NotBlank(message = "分享的分享码不能为空")
    private String shareCode;
}
