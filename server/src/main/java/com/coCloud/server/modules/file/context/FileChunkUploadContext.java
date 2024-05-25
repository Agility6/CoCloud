package com.coCloud.server.modules.file.context;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * ClassName: FileChunkUploadContext
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 15:50
 * @Version: 1.0
 */
@Data
public class FileChunkUploadContext implements Serializable {

    private static final long serialVersionUID = -6702179902168262922L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 总体的分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片下标
     * 从1开始
     */
    private Integer chunkNumber;

    /**
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     *  文件的总大小
     */
    private Long totalSize;

    /**
     * 文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
