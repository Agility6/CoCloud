package com.coCloud.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: FileSearchContext
 * Description: 搜索文件上下文实体信息
 *
 * @Author agility6
 * @Create 2024/6/2 20:24
 * @Version: 1.0
 */
@Data
public class FileSearchContext implements Serializable {

    private static final long serialVersionUID = 6419013724942344503L;

    /**
     * 搜索的关键字
     */
    private String keyword;

    /**
     * 搜素的文件类型集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
