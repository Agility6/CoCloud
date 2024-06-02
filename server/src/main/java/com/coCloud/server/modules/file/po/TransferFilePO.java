package com.coCloud.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: TransferFilePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/2 0:27
 * @Version: 1.0
 */
@ApiModel("文件转移参数实体对象")
@Data
public class TransferFilePO implements Serializable {

    private static final long serialVersionUID = 5101536460141989127L;

    @ApiModelProperty("要转移的文件ID集合，多个使用公用分隔符隔开")
    @NotBlank(message = "请选择要转移的文件")
    private String fileIds;

    @ApiModelProperty("要转移到的目标文件夹的ID")
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;
}
