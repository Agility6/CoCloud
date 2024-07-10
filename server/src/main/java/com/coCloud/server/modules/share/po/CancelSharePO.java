package com.coCloud.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: CancelSharePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/10 11:08
 * @Version: 1.0
 */
@ApiModel("取消分享参数实体对象")
@Data
public class CancelSharePO implements Serializable {

    private static final long serialVersionUID = -3762487810698275136L;

    @ApiModelProperty(value = "要取消的分享ID的集合，多个使用公共分隔符")
    @NotBlank(message = "请选择要取消的分享")
    private String shareIds;
}
