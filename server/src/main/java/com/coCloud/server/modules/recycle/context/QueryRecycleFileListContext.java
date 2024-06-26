package com.coCloud.server.modules.recycle.context;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryRecycleFileListContext
 * Description: 查找用户回收站文件列表上下文实体对象
 *
 * @Author agility6
 * @Create 2024/6/22 18:01
 * @Version: 1.0
 */
@Data
public class QueryRecycleFileListContext implements Serializable {

    private static final long serialVersionUID = -1145150433678761923L;

    private Long userId;
}
