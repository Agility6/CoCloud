package com.coCloud.server.modules.share.service;

import com.coCloud.server.modules.share.context.SaveShareFilesContext;
import com.coCloud.server.modules.share.entity.CoCloudShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author agility6
 * @description 针对表【co_cloud_share_file(用户分享文件表)】的数据库操作Service
 * @createDate 2024-05-10 19:23:23
 */
public interface IShareFileService extends IService<CoCloudShareFile> {

    /**
     * 保存分享的文件的对应关系
     *
     * @param saveShareFilesContext
     */
    void saveShareFiles(SaveShareFilesContext context);
}
