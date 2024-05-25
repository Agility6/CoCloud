package com.coCloud.server.modules.file.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: UploadedChunksVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 16:43
 * @Version: 1.0
 */
@ApiModel("查询用户已上传的文件分片列表返回实体")
@Data
public class UploadedChunksVO implements Serializable {

    private static final long serialVersionUID = -5821327689638271399L;

    @ApiModelProperty("已上传的分片编号列表")
    private List<Integer> uploadedChunks;
}
