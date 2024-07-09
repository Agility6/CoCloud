package com.coCloud.server.modules.share.service;

import com.coCloud.server.modules.share.context.CreateShareUrlContext;
import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlVO;

/**
 * @author agility6
 * @description 针对表【co_cloud_share(用户分享表)】的数据库操作Service
 * @createDate 2024-05-10 19:23:23
 */
public interface IShareService extends IService<CoCloudShare> {

    /**
     * 创建分享链接
     *
     * @param context
     * @return
     */
    CoCloudShareUrlVO create(CreateShareUrlContext context);
}
