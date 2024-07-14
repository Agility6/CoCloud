package com.coCloud.server.modules.share.context;

import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.coCloud.server.modules.share.vo.ShareSimpleDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryShareSimpleDetailContext
 * Description: 查询分享简单详情上下文实体信息
 *
 * @Author agility6
 * @Create 2024/7/14 22:13
 * @Version: 1.0
 */
@Data
public class QueryShareSimpleDetailContext implements Serializable {

    private static final long serialVersionUID = 8199765046904847381L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享对应的实体信息
     */
    private CoCloudShare record;

    /**
     * 简单分享详情的VO对象
     */
    private ShareSimpleDetailVO vo;

}
