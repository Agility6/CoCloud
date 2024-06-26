package com.coCloud.server.modules.recycle.service.impl;

import com.coCloud.server.modules.file.context.QueryFileListContext;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.recycle.context.QueryRecycleFileListContext;
import com.coCloud.server.modules.recycle.service.IRecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: RecycleServiceImpl
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/22 17:59
 * @Version: 1.0
 */
@Service
public class RecycleServiceImpl implements IRecycleService {

    @Autowired
    private IUserFileService iUserFileService;

    /**
     * 查询用户的回收站文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<CoCloudUserFileVO> recycles(QueryRecycleFileListContext context) {
        // 创建QueryFileListContext
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        // 设置参数
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        // 调用UserFileService的getFileList
        return iUserFileService.getFileList(queryFileListContext);
    }
}
