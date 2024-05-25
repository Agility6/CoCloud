package com.coCloud.server.modules.file.context;

import com.coCloud.server.modules.file.entity.CoCloudFile;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: FileChunkMergeContext
 * Description: 文件分片合并的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/5/25 17:04
 * @Version: 1.0
 */
@Data
public class FileChunkMergeContext implements Serializable {

    private static final long serialVersionUID = 5137623984143687420L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 文件的父文件夹ID
     */
    private Long parentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 物理文件记录
     */
    private CoCloudFile record;
}
