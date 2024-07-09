package com.coCloud.server.modules.share.context;

import com.coCloud.server.modules.share.entity.CoCloudShare;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: CreateShareUrlContext
 * Description: 创建分享链接上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/9 14:28
 * @Version: 1.0
 */
@Data
public class CreateShareUrlContext implements Serializable {

    private static final long serialVersionUID = 7608871803570453154L;

    /**
     * 分享的名称
     */
    private String shareName;

    /**
     * 分享的类型
     */
    private Integer shareType;

    /**
     * 分享的日期类型
     */
    private Integer shareDayType;

    /**
     * 该分项对应的文件ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 已经保存的分享实体信息
     */
    private CoCloudShare record;

}
