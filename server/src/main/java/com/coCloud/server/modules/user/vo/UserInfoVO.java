package com.coCloud.server.modules.user.vo;

import com.coCloud.web.serializer.IdEncryptSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: UserInfoVO
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/15 23:51
 * @Version: 1.0
 */
@Data
@ApiModel(value = "用户基本信息实体")
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = -3913136233676196561L;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户根目录的加密ID")
    @JsonSerialize(using = IdEncryptSerializer.class) // Long进行序列化
    private Long rootFileId;

    @ApiModelProperty("用户根目录名称")
    private String rootFilename;
}
