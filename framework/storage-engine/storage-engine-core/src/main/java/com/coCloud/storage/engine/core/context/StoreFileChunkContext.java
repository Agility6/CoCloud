package com.coCloud.storage.engine.core.context;

import com.sun.org.apache.bcel.internal.generic.LineNumberGen;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import lombok.Data;

import java.io.InputStream;
import java.io.Serializable;

/**
 * ClassName: StoreFileChunkContext
 * Description: 保存文件分片的上下文信息
 *
 * @Author agility6
 * @Create 2024/5/25 16:26
 * @Version: 1.0
 */
@Data
public class StoreFileChunkContext implements Serializable {

    private static final long serialVersionUID = 2407667118286289579L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片的下标
     */
    private Integer chunkNumber;

    /*
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 当前登录用户的ID
     */
    private Long userId;
}
