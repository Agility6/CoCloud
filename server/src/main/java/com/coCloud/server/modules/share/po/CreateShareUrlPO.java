package com.coCloud.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: CreateShareUrlPO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/9 14:25
 * @Version: 1.0
 */
@ApiModel(value = "创建分享链接的参数对象实体")
@Data
public class CreateShareUrlPO implements Serializable {

    private static final long serialVersionUID = 1266628711509735714L;

    @ApiModelProperty(value = "分享的名称", required = true)
    @NotBlank(message = "分享名称不能为空")
    private String shareName;

    @ApiModelProperty(value = "分享的类型", required = true)
    @NotBlank(message = "分享类型不能为空")
    private Integer shareType;

    @ApiModelProperty(value = "分享的日期类型", required = true)
    @NotBlank(message = "分享日期类型不能为空")
    private Integer shareDayType;

    @ApiModelProperty(value = "分享的文件ID集合，多个使用公用分隔符", required = true)
    @NotBlank(message = "分享文件ID不能为空")
    private String shareFileIds;
}
