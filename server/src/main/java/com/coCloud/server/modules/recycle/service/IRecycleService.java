package com.coCloud.server.modules.recycle.service;

import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.recycle.context.QueryRecycleFileListContext;

import java.util.List;

/**
 * ClassName: IRecycleService
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/22 17:58
 * @Version: 1.0
 */
public interface IRecycleService {

    /**
     * 查找用户的回收站文件列表
     *
     * @param context
     * @return
     */
    List<CoCloudUserFileVO> recycles(QueryRecycleFileListContext context);
}
