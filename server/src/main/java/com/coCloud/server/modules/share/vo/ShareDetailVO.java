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
 * ClassName: ShareDetailVo
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/13 21:12
 * @Version: 1.0
 */
@ApiModel("分享详情的返回实体对象")
@Data
public class ShareDetailVO implements Serializable {

    private static final long serialVersionUID = 1395564362796363402L;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @ApiModelProperty("分享的ID")
    private Long shareId;

    @ApiModelProperty("分享的名称")
    private String shareName;

    @JsonSerialize(using = Date2StringSerializer.class)
    @ApiModelProperty("分享的创建时间")
    private Date createTime;

    @ApiModelProperty("分享的过期类型")
    private Integer shareDay;

    @ApiModelProperty("分享的截止时间")
    @JsonSerialize(using = Date2StringSerializer.class)
    private Date shareEndTime;

    @ApiModelProperty("分享的文件列表")
    private List<CoCloudUserFileVO> coCloudUserFileVOList;

    @ApiModelProperty("分享者的信息")
    private ShareUserInfoVO shareUserInfoVO;
}
