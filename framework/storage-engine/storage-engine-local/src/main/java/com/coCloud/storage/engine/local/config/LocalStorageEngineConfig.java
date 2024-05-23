package com.coCloud.storage.engine.local.config;

import com.coCloud.core.utils.FileUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName: LocalStorageEngineConfig
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/23 16:22
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "com.co-cloud.storage.engine.local")
@Data
public class LocalStorageEngineConfig {

    /**
     * 实际存放路径的前缀
     */
    private String rootFilePath = FileUtils.generateDefaultStoreFileRealPath();

    /**
     * 实际存放文件分片的路径的前缀
     */
    private String rootFileChunkPath = FileUtils.generateDefaultStoreFileChunkRealPath();

}
