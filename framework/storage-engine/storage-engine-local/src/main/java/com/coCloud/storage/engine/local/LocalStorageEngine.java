package com.coCloud.storage.engine.local;

import com.coCloud.core.utils.FileUtils;
import com.coCloud.storage.engine.core.AbstractStorageEngine;
import com.coCloud.storage.engine.core.context.*;
import com.coCloud.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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

    /**
     * 执行保存文件分片
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        // 本质和保存单文件逻辑一致
        String basePath = config.getRootFileChunkPath();
        String realFilePath = FileUtils.generateStoreFileChunkRealPath(basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        // 将realFilePath保存到context中
        context.setRealPath(realFilePath);
    }

    /**
     * 执行文件分片动作
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        // 获取基础路径
        String basePath = config.getRootFilePath();
        // 生成真实路径
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        // 创建文件
        FileUtils.createFile(new File(realFilePath));
        // 获取所有的分片路径
        List<String> chunkPaths = context.getRealPathList();
        // 合并
        for (String chunkPath : chunkPaths) {
            FileUtils.appendWrite(Paths.get(realFilePath), new File(chunkPath).toPath());
        }

        // 根据分片路径删除文件
        FileUtils.deleteFiles(chunkPaths);

        // context设置合并完成后的路径
        context.setRealPath(realFilePath);

    }

    /**
     * 读取文件内容并写入到输出流中
     *
     * @param context
     * @throws IOException
     */
    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        // 获取文件
        File file = new File(context.getRealPath());
        // 写入到输出流中
        FileUtils.writeFile2OutputStream(new FileInputStream(file), context.getOutputStream(), file.length());
    }
}
