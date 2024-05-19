package com.coCloud.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: QueryFileListContext
 * Description: 查询文件列表上下文实体
 *
 * @Author agility6
 * @Create 2024/5/16 20:41
 * @Version: 1.0
 */
@Data
public class QueryFileListContext implements Serializable {

    private static final long serialVersionUID = -8688778441192974694L;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 文件类型的集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 当前的登录用户
     */
    private Long userId;

    /**
     * 文件的删除标识
     */
    private Integer delFlag;

    /**
     * 文件ID集合
     */
    private List<Long> fileIdList;

}
