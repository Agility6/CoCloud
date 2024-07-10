package com.coCloud.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: CancelShareContext
 * Description: 取消分享的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/10 11:09
 * @Version: 1.0
 */
@Data
public class CancelShareContext implements Serializable {

    private static final long serialVersionUID = 3853395751444306155L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要取消的分享ID集合
     */
    private List<Long> shareIdList;

}
