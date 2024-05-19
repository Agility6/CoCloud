package com.coCloud.server;

import com.coCloud.core.constants.CoCloudConstants;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ClassName: coCloudServerLauncher
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/10 11:59
 * @Version: 1.0
 */
@SpringBootApplication(scanBasePackages = CoCloudConstants.BASE_COMPONENT_SCAN_PATH)
@ServletComponentScan(basePackages = CoCloudConstants.BASE_COMPONENT_SCAN_PATH)
@EnableTransactionManagement // 支持事务
@MapperScan(basePackages = CoCloudConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
public class CoCloudServerLauncher {
    public static void main(String[] args) {
        SpringApplication.run(CoCloudServerLauncher.class);
    }
}
