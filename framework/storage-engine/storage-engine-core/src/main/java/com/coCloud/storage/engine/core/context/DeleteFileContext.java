package com.coCloud.storage.engine.core.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: DeleteFileContext
 * Description: 删除物理文件的上下文试实体信息
 *
 * @Author agility6
 * @Create 2024/5/23 0:16
 * @Version: 1.0
 */
@Data
public class DeleteFileContext implements Serializable {

    private static final long serialVersionUID = -499334606813871488L;

    /**
     * 要删除的物理文件路径的集合
     */
    private List<String> realFilePathList;
}
