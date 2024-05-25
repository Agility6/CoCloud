package com.coCloud.storage.engine.oss;

import com.coCloud.storage.engine.core.AbstractStorageEngine;
import com.coCloud.storage.engine.core.context.DeleteFileContext;
import com.coCloud.storage.engine.core.context.MergeFileContext;
import com.coCloud.storage.engine.core.context.StoreFileChunkContext;
import com.coCloud.storage.engine.core.context.StoreFileContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ClassName: OSSStorageEngine
 * Description: 对接阿里云OSS的文件存储引擎实现方案
 *
 * @Author agility6
 * @Create 2024/5/20 20:35
 * @Version: 1.0
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {
    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {

    }

    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {

    }
}
