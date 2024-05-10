package com.coCloud.server;

import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.response.R;
import io.swagger.annotations.Api;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

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
@RestController
@Api("测试接口类")
@Validated
@EnableTransactionManagement // 支持事务
@MapperScan(basePackages = CoCloudConstants.BASE_COMPONENT_SCAN_PATH + ".server.modules.**.mapper")
public class coCloudServerLauncher {
    @Autowired
    private static Environment environment;
    public static void main(String[] args) {
        SpringApplication.run(coCloudServerLauncher.class);
    }

    @GetMapping("/hello")
    public R<String> hello(@NotBlank(message = "name cannot be empty!") String name) {
        return R.success("hello" + name + "!");
    }

}
