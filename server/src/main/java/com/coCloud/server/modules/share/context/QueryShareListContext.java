package com.coCloud.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryShareListContext
 * Description: 查询用户已有的分享链接列表的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/10 11:01
 * @Version: 1.0
 */
@Data
public class QueryShareListContext implements Serializable {

    private static final long serialVersionUID = -4203244654984304427L;

    /**
     * 当前登录等的用户ID
     */
    private Long userId;
}
