package com.coCloud.server.modules.recycle.po;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: DeletePO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/7 20:03
 * @Version: 1.0
 */
@ApiModel("文件删除参数实体")
@Data
public class DeletePO implements Serializable {

    private static final long serialVersionUID = 1128035361236403512L;

    @ApiModelProperty(value = "要删除的文件ID集合，多个使用公用分隔符分隔", required = true)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;
}
