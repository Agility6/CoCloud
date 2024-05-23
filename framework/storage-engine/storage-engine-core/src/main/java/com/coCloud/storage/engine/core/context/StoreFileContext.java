package com.coCloud.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * ClassName: StoreFileContext
 * Description: 文件存储引擎物理文件的上下文实体
 *
 * @Author agility6
 * @Create 2024/5/23 0:41
 * @Version: 1.0
 */
@Data
public class StoreFileContext implements Serializable {

    private static final long serialVersionUID = -4557497097678572518L;

    /**
     * 上传的文件名称
     */
    private String filename;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件的输入流信息
     */
    private InputStream inputStream;

    /**
     * 文件上传后的物理路径
     */
    private String realPath;
}
