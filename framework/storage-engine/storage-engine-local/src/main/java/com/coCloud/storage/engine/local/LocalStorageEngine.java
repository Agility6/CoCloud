package com.coCloud.storage.engine.local;

import com.coCloud.core.utils.FileUtils;
import com.coCloud.storage.engine.core.AbstractStorageEngine;
import com.coCloud.storage.engine.core.context.DeleteFileContext;
import com.coCloud.storage.engine.core.context.StoreFileContext;
import com.coCloud.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * ClassName: LocalStorageEngine
 * Description: 本地的文件存储引擎实现方案
 *
 * @Author agility6
 * @Create 2024/5/20 20:31
 * @Version: 1.0
 */
@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Autowired
    private LocalStorageEngineConfig config;

    /**
     * 执行保存物理文件的动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        // 获取文件上传的根目录
        String basePath = config.getRootFilePath();
        // 生成实际路径
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        // 将文件的输入流写入到文件中
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        // 设置真实路径到上下文中
        context.setRealPath(realFilePath);
    }

    /**
     * 执行删除物理文件的动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtils.deleteFiles(context.getRealFilePathList());
    }
}
