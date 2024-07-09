package com.coCloud.server.common.config;

import com.coCloud.core.constants.CoCloudConstants;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName: CoCloudServerConfig
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 16:21
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "com.co-cloud.server")
@Data
public class CoCloudServerConfig {

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = CoCloudConstants.ONE_INT;

    /**
     * 分享链接的前缀
     */
    private String sharePrefix = "http://127.0.0.1:" + serverPort + "/share/";

}
