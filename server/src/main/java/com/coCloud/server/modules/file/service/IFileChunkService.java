package com.coCloud.server.modules.file.service;

import com.coCloud.server.modules.file.context.FileChunkSaveContext;
import com.coCloud.server.modules.file.entity.CoCloudFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coCloud.server.modules.file.vo.FileChunkUploadVO;

/**
 * @author agility6
 * @description 针对表【co_cloud_file_chunk(文件分片信息表)】的数据库操作Service
 * @createDate 2024-05-10 19:22:09
 */
public interface IFileChunkService extends IService<CoCloudFileChunk> {

    /**
     * 文件分片保存
     *
     * @param context
     * @return
     */
    void saveChunkFile(FileChunkSaveContext context);
}
