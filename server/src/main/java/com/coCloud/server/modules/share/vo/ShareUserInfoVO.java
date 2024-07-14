package com.coCloud.server.modules.share.vo;

import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.web.serializer.Date2StringSerializer;
import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * ClassName: ShareUserInfoVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/13 21:16
 * @Version: 1.0
 */
@ApiModel("分享者信息返回实体对象")
@Data
public class ShareUserInfoVO implements Serializable {

    private static final long serialVersionUID = -5257699110082254046L;

    @ApiModelProperty("分享者的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    @ApiModelProperty("分享者的名称")
    private String username;
}
