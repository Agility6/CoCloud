package com.coCloud.storage.engine.core;

import com.coCloud.storage.engine.core.context.DeleteFileContext;
import com.coCloud.storage.engine.core.context.MergeFileContext;
import com.coCloud.storage.engine.core.context.StoreFileChunkContext;
import com.coCloud.storage.engine.core.context.StoreFileContext;

import java.io.IOException;

/**
 * ClassName: StorageEngine
 * Description: 文件存储引擎模块的顶级接口
 * <p>
 * 该文件定义所有需要向外暴露给业务层面的相关文件操作的功能
 * 业务方只能调用该接口的方法，而不能直接使用具体的实现方案去做业务调用
 *
 * @Author agility6
 * @Create 2024/5/20 20:28
 * @Version: 1.0
 */
public interface StorageEngine {

    /**
     * 存储物理文件
     *
     * @param context
     * @throws IOException
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     *
     * @param deleteFileContext
     */
    void delete(DeleteFileContext deleteFileContext) throws IOException;

    /**
     * 存储物理文件的分片
     *
     * @param context
     */
    void storeChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 合并文件分片
     *
     * @param context
     */
    void mergeFile(MergeFileContext context) throws IOException;
}
