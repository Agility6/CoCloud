package com.coCloud.server.modules.share.context;

import com.coCloud.server.modules.share.entity.CoCloudShare;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryChildFileListContext
 * Description: 查询下一级文件列表的上下文实体信息
 *
 * @Author agility6
 * @Create 2024/7/15 12:58
 * @Version: 1.0
 */
@Data
public class QueryChildFileListContext implements Serializable {

    private static final long serialVersionUID = -1730221107060597445L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 父文件夹的ID
     */
    private Long parentId;

    /**
     * 分享对应的实体信息
     */
    private CoCloudShare record;

}
