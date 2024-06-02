package com.coCloud.server.modules.file.vo;

import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * ClassName: BreadcrumbVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/3 0:04
 * @Version: 1.0
 */
@ApiModel("面包屑列表展示实体")
@Data
public class BreadcrumbVO implements Serializable {

    private static final long serialVersionUID = 4346314487263028344L;

    @ApiModelProperty("文件ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long id;

    @ApiModelProperty("父文件夹ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long parentId;

    @ApiModelProperty("文件夹名称")
    private String name;

    /**
     * 实体转换
     *
     * @param record
     * @return
     */
    public static BreadcrumbVO transfer(CoCloudUserFile record) {
        BreadcrumbVO vo = new BreadcrumbVO();

        if (Objects.nonNull(record)) {
            vo.setId(record.getFileId());
            vo.setParentId(record.getParentId());
            vo.setName(record.getFilename());
        }

        return vo;
    }
}
