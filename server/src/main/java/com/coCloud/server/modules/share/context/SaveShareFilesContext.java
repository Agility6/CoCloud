package com.coCloud.server.modules.share.context;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: SaveShareFilesContext
 * Description: 保存文件和分享的关联关系上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/9 15:14
 * @Version: 1.0
 */
@Data
public class SaveShareFilesContext implements Serializable {

    private static final long serialVersionUID = -1015603159011543224L;

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享对应的文件的ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

}
