package com.coCloud.server.modules.file.service;

import com.coCloud.server.modules.file.context.QueryRealFileListContext;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author agility6
 * @description 针对表【co_cloud_file(物理文件信息表)】的数据库操作Service
 * @createDate 2024-05-10 19:22:09
 */
public interface IFileService extends IService<CoCloudFile> {

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    List<CoCloudFile> getFileList(QueryRealFileListContext context);
}