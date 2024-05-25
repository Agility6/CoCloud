package com.coCloud.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryUploadedChunksContext
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 16:46
 * @Version: 1.0
 */
@Data
public class QueryUploadedChunksContext implements Serializable {

    private static final long serialVersionUID = 8874529845642339862L;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
