package com.coCloud.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: FileChunkUploadVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 15:46
 * @Version: 1.0
 */
@ApiModel("文件分片上传的响应实体")
@Data
public class FileChunkUploadVO implements Serializable {

    private static final long serialVersionUID = 931678837662988703L;

    @ApiModelProperty("是否需要合并 0 不需要 1 需要")
    private Integer mergeFlag;
}
