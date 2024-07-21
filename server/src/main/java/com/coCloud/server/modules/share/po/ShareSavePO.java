package com.coCloud.server.modules.share.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: ShareSavePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/16 11:09
 * @Version: 1.0
 */
@ApiModel("保存至我的网盘参数实体对象")
@Data
public class ShareSavePO implements Serializable {

    private static final long serialVersionUID = 3491437149407538210L;

    @ApiModelProperty(value = "要转存的文件ID集合，多个使用公用分隔符拼接", required = true)
    @NotBlank(message = "请选择要保存的文件")
    private String fileIds;

    @ApiModelProperty(value = "要转存到的文件夹ID", required = true)
    @NotBlank(message = "请选择要保存到的文件夹")
    private String targetParentId;

}
