package com.coCloud.server.modules.recycle.context;

import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: DeleteContext
 * Description: 删除文件的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/7 20:06
 * @Version: 1.0
 */
@Data
public class DeleteContext implements Serializable {

    private static final long serialVersionUID = 4832429120107089034L;

    /**
     * 要操作的文件ID的集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要被删除的文件记录列表
     */
    private List<CoCloudUserFile> records;

    /**
     * 所有要被删除的文件记录列表
     */
    private List<CoCloudUserFile> allRecords;
}
