package com.coCloud.server.modules.recycle.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: RestorePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/2 21:22
 * @Version: 1.0
 */
@ApiModel("文件还原参数实体")
@Data
public class RestorePO implements Serializable {

    private static final long serialVersionUID = -8651552717809067364L;

    @ApiModelProperty(value = "要还原的文件ID集合，多个使用公用分隔符分隔", required = true)
    @NotBlank(message = "请选择要还原的文件")
    private String fileIds;
}
