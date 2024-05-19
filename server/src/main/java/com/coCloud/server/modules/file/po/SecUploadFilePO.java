package com.coCloud.server.modules.file.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.awt.*;
import java.io.Serializable;

/**
 * ClassName: SecUploadFilePO
 * Description: 妙传文件接口参数对象实体
 *
 * @Author agility6
 * @Create 2024/5/19 23:00
 * @Version: 1.0
 */
@ApiModel("妙传文件接口参数对象实体")
@Data
public class SecUploadFilePO implements Serializable {

    private static final long serialVersionUID = 3486035129471361689L;

    @ApiModelProperty(value = "秒传的父文件夹ID", required = true)
    @NotBlank(message = "父文件夹ID不能为空")
    private String parentId;

    @ApiModelProperty(value = "文件名称", required = true)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @ApiModelProperty(value = "文件的唯一标识", required = true)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}
