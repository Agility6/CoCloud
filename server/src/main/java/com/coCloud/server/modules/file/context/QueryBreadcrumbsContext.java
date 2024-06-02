package com.coCloud.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryBreadcrumbsContext
 * Description: 搜素文件面包屑列表的上下文信息实体
 *
 * @Author agility6
 * @Create 2024/6/3 0:07
 * @Version: 1.0
 */
@Data
public class QueryBreadcrumbsContext implements Serializable {

    private static final long serialVersionUID = -4175442382818014949L;

    /**
     *  文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户IDkj
     */
    private Long userId;
}
