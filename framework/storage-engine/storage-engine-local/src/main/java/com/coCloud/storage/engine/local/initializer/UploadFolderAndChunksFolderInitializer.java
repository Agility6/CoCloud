package com.coCloud.storage.engine.local.initializer;

import com.coCloud.storage.engine.local.config.LocalStorageEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * ClassName: UploadFolderAndChunksFolderInitializer
 * Description: 初始化上传文件根目录和文件分片存储根目录的初始化器
 *
 * @Author agility6
 * @Create 2024/6/6 22:26
 * @Version: 1.0
 */
@Component
@Slf4j
public class UploadFolderAndChunksFolderInitializer implements CommandLineRunner {

    @Autowired
    private LocalStorageEngineConfig config;

    @Override
    public void run(String... args) throws Exception {
        // 创建目录
        FileUtils.forceMkdir(new File(config.getRootFilePath()));
        log.info("the root file path has been created!!!");
        FileUtils.forceMkdir(new File(config.getRootFileChunkPath()));
        log.info("the root file chunk path has been created!");
    }
}
