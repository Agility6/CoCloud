package com.coCloud.server.modules.file.vo;

import com.alibaba.fastjson.JSON;
import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: FolderTreeNodeVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/1 16:26
 * @Version: 1.0
 */
@ApiModel("文件夹树节点实体")
@Data
public class FolderTreeNodeVO implements Serializable {

    private static final long serialVersionUID = -4859989335943739839L;

    @ApiModelProperty("文件夹名称")
    private String label;

    @ApiModelProperty("文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty("父文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("子节点集合")
    private List<FolderTreeNodeVO> children;

    public void print() {
        String jsonString = JSON.toJSONString(this);
        System.out.println(jsonString);
    }
}
