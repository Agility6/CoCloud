package com.coCloud.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * ClassName: ReadFileContext
 * Description: 文件读取的上下文实体信息
 *
 * @Author agility6
 * @Create 2024/6/1 15:57
 * @Version: 1.0
 */
@Data
public class ReadFileContext implements Serializable {

    private static final long serialVersionUID = -8222103632090305791L;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的输出流
     */
    private OutputStream outputStream;
}
