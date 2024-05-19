package com.coCloud.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: CreateFolderPO
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/18 15:24
 * @Version: 1.0
 */
@ApiModel(value = "创建文件夹参数实体")
@Data
public class CreateFolderPO implements Serializable {

    private static final long serialVersionUID = -1211526479884069189L;

    @ApiModelProperty(value = "加密的父文件夹ID", required = true)
    @NotBlank(message = "父文件ID不能为空")
    private String parentId;

    @ApiModelProperty(value = "文件夹名称", required = true)
    @NotBlank(message = "文件夹名称不能为空")
    private String folderName;
}
