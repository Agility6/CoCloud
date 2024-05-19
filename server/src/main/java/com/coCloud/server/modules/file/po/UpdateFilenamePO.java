package com.coCloud.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: UpdateFilenamePO
 * Description: 文件重命名参数对象
 *
 * @Author agility6
 * @Create 2024/5/19 19:30
 * @Version: 1.0
 */
@Data
@ApiModel(value = "文件重命名参数对象")
public class UpdateFilenamePO implements Serializable {

    private static final long serialVersionUID = 1652733894151027934L;

    @ApiModelProperty(value = "更新的文件ID", required = true)
    @NotBlank(message = "更新的文件ID不能为空")
    private String fileId;

    @ApiModelProperty(value = "新的文件名称", required = true)
    @NotBlank(message = "新的文件名称不能为空")
    private String newFilename;
}
