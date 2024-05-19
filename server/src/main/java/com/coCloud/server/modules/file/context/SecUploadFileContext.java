package com.coCloud.server.modules.file.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * ClassName: SecUploadFileContext
 * Description: 妙传文件接口参数对象实体
 *
 * @Author agility6
 * @Create 2024/5/19 23:00
 * @Version: 1.0
 */
@Data
public class SecUploadFileContext implements Serializable {

    private static final long serialVersionUID = 4825723797996096056L;

    /**
     * 文件的父ID
     */
    private Long parentId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前登录用的ID
     */
    private Long userId;
}
