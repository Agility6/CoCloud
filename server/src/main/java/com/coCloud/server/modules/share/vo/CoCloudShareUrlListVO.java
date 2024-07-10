package com.coCloud.server.modules.share.vo;

import com.coCloud.web.serializer.Date2StringSerializer;
import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName: CoCloudShareUrlListVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/10 10:57
 * @Version: 1.0
 */
@ApiModel("分享链接列表结果实体对象")
@Data
public class CoCloudShareUrlListVO implements Serializable {

    private static final long serialVersionUID = -914462385604857362L;

    @ApiModelProperty("分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享的名称")
    private String shareName;

    @ApiModelProperty("分享的URL")
    private String shareUrl;

    @ApiModelProperty("分享的分享码")
    private String shareCode;

    @ApiModelProperty("分享的状态")
    private Integer shareStatus;

    @ApiModelProperty("分享的类型")
    private Integer shareType;

    @ApiModelProperty("分享的过期类型")
    private Integer shareDayType;

    @ApiModelProperty("分享的过期时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @ApiModelProperty("分享的创建时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date createTime;
}
