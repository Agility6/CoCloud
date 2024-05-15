package com.coCloud.server.modules.file.service;

import com.coCloud.server.modules.file.context.CreateFolderContext;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author agility6
 * @description 针对表【co_cloud_user_file(用户文件信息表)】的数据库操作Service
 * @createDate 2024-05-10 19:22:09
 */
public interface IUserFileService extends IService<CoCloudUserFile> {

    /**
     * 创建文件夹信息
     *
     * @param createFolderContext
     * @return
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 查询用户的根文件夹信息
     *
     * @param userId
     * @return
     */
    CoCloudUserFile getUserRootFile(Long userId);
}
