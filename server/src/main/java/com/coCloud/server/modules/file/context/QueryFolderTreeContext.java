package com.coCloud.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryFolderTreeContext
 * Description: 查询文件夹树的上下文实体信息
 *
 * @Author agility6
 * @Create 2024/6/1 16:29
 * @Version: 1.0
 */
@Data
public class QueryFolderTreeContext implements Serializable {

    private static final long serialVersionUID = 8157520992684451803L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
