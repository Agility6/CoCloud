package com.coCloud.server.modules.file.context;

import com.coCloud.server.modules.file.entity.CoCloudFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * ClassName: FileSaveContext
 * Description: 保存单文件的上下文实体
 *
 * @Author agility6
 * @Create 2024/5/22 23:41
 * @Version: 1.0
 */
@Data
public class FileSaveContext implements Serializable {

    private static final long serialVersionUID = 9123409895614937062L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 实体文件记录
     */
    private CoCloudFile record;

    /**
     * 文件上传的物理路径
     */
    private String realPath;
}
