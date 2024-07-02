package com.coCloud.server.modules.recycle.context;

import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: RestoreContext
 * Description: 还原文件的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/2 21:24
 * @Version: 1.0
 */
@Data
public class RestoreContext implements Serializable {

    private static final long serialVersionUID = -6841269382443292879L;

    /**
     * 要操作的文件ID的集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登陆的用户ID
     */
    private Long userId;

    /**
     * 要被还原的文件记录列表
     */
    private List<CoCloudUserFile> records;
}
