package com.coCloud.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: ShareSaveContext
 * Description: 保存到我的网盘上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/16 11:11
 * @Version: 1.0
 */
@Data
public class ShareSaveContext implements Serializable {

    private static final long serialVersionUID = 8846818930611515576L;

    /**
     * 目标文件夹ID
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
     * 分享的ID
     */
    private Long shareId;
}
