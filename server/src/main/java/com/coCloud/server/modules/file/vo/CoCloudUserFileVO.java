package com.coCloud.server.modules.file.vo;

import com.coCloud.web.serializer.Date2StringSerializer;
import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: CoCloudUserFileVO
 * Description: 用户查询文件列表相应实体
 *
 * @Author agility6
 * @Create 2024/5/16 20:32
 * @Version: 1.0
 */
@Data
@ApiModel(value = "文件列表相应实体")
public class CoCloudUserFileVO implements Serializable {

    private static final long serialVersionUID = 1246507315658669585L;

    @JsonSerialize(using = IdEncryptSerializer.class) // Id自动加密的JSON序列化器
    @ApiModelProperty(value = "文件ID")
    private Long fileId;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @ApiModelProperty(value = "父文件夹ID")
    private Long parentId;

    @ApiModelProperty(value = "文件名称")
    private String filename;

    @ApiModelProperty(value = "文件大小描述")
    private String fileSizeDesc;

    @ApiModelProperty(value = "文件夹标识 0 否 1 是")
    private Integer folderFlag;

    @ApiModelProperty(value = "文件类型 1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv")
    private Integer fileType;

    @ApiModelProperty(value = "文件更新时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date updateTime;

}