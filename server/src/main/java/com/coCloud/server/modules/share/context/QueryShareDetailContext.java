package com.coCloud.server.modules.share.context;

import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.coCloud.server.modules.share.vo.ShareDetailVO;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryShareDetailContext
 * Description: 查询分享详情的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/13 21:19
 * @Version: 1.0
 */
@Data
public class QueryShareDetailContext implements Serializable {

    private static final long serialVersionUID = -4108180962502073729L;

    /**
     * 对应的分享ID
     */
    private Long shareId;

    /**
     * 分享实体
     */
    private CoCloudShare record;

    /**
     * 分享详情的VO对象
     */
    private ShareDetailVO vo;
}
