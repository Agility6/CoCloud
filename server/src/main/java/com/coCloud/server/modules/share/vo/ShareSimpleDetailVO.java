package com.coCloud.server.modules.share.vo;

import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: ShareSimpleDetailVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/14 22:11
 * @Version: 1.0
 */
@ApiModel("查询分享简单详情返回实体对象")
@Data
public class ShareSimpleDetailVO implements Serializable {

    private static final long serialVersionUID = 7858479646962336288L;

    @ApiModelProperty("分享Id")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @ApiModelProperty("分享名称")
    private String shareName;

    @ApiModelProperty("分享人信息")
    private ShareUserInfoVO shareUserInfoVO;
}
