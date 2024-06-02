package com.coCloud.storage.engine.fd;

import com.coCloud.storage.engine.core.AbstractStorageEngine;
import com.coCloud.storage.engine.core.context.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * ClassName: FastDFSStorageEngine
 * Description: FastDFS文件存储引擎的实现方案
 *
 * @Author agility6
 * @Create 2024/5/20 20:27
 * @Version: 1.0
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {
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

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {

    }
}
