package com.coCloud.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: DeleteFilePO
 * Description: 批量删除文件实体对象
 *
 * @Author agility6
 * @Create 2024/5/19 21:20
 * @Version: 1.0
 */
@ApiModel(value = "批量删除文件入参对象实体")
@Data
public class DeleteFilePO implements Serializable {

    private static final long serialVersionUID = 3737130148952926948L;

    @ApiModelProperty(value = "要删除的文件ID，多个使用公用的分隔符分割", required = true)
    @NotBlank(message = "请选择要删除的文件信息")
    private String fileIds;
}
