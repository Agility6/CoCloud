package com.coCloud.server.modules.share.vo;

import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: CoCloudShareUrlVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/9 14:21
 * @Version: 1.0
 */
@ApiModel(value = "创建分享链接的返回实体对象")
@Data
public class CoCloudShareUrlVO implements Serializable {

    private static final long serialVersionUID = -4396234604689917451L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @ApiModelProperty("分享链接的ID")
    private Long shareId;

    @ApiModelProperty("分享链接的URL")
    private String shareName;

    @ApiModelProperty("分享链接的URL")
    private String shareUrl;

    @ApiModelProperty("分享链接的分享码")
    private String shareCode;

    @ApiModelProperty("分享链接的状态")
    private Integer shareStatus;
}
