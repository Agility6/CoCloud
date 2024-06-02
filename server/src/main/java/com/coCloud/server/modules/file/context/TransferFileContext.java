package com.coCloud.server.modules.file.context;

import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: TransferFileContext
 * Description: 文件转移操作上下文实体对象
 *
 * @Author agility6
 * @Create 2024/6/2 15:33
 * @Version: 1.0
 */
@Data
public class TransferFileContext implements Serializable {

    private static final long serialVersionUID = 1417119183716951427L;

    /**
     * 要转移的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要转移的文件列表
     */
    private List<CoCloudUserFile> prepareRecords;
}
