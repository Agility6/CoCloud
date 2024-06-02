package com.coCloud.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: CopyFilePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/2 19:30
 * @Version: 1.0
 */
@ApiModel("文件复制参数实体对象")
@Data
public class CopyFilePO implements Serializable {

    private static final long serialVersionUID = -4900810990420682677L;

    @ApiModelProperty("要复制的文件ID集合，多个使用公用分隔符隔开")
    @NotBlank(message = "请选择要复制的文件")
    private String fileIds;

    @ApiModelProperty("要转移的目标文件夹的ID")
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;

}
