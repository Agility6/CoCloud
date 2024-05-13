package com.coCloud.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: CreateFolderContext
 * Description: 创建文件夹上下文实体
 *
 * @Author agility6
 * @Create 2024/5/13 11:57
 * @Version: 1.0
 */
@Data
public class CreateFolderContext implements Serializable {

    private static final long serialVersionUID = -3518242967274257009L;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String folderName;

}
