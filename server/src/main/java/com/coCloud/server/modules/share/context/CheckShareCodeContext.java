package com.coCloud.server.modules.share.context;

import com.coCloud.server.modules.share.entity.CoCloudShare;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: CheckShareCodeContext
 * Description: 校验分享码上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/10 14:15
 * @Version: 1.0
 */
@Data
public class CheckShareCodeContext implements Serializable {

    private static final long serialVersionUID = 6338411743996318988L;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 对应的分享实体
     */
    private CoCloudShare record;
}
