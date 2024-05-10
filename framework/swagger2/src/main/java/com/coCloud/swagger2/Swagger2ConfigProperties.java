package com.coCloud.swagger2;

import com.coCloud.core.constants.CoCloudConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName: Swagger2ConfigProperties
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/10 13:06
 * @Version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "swagger2")
public class Swagger2ConfigProperties {

    private boolean show = true;

    private String groupName = "CoCloud";

    private String basePackage = CoCloudConstants.BASE_COMPONENT_SCAN_PATH;

    private String title = "coCloud-server";

    private String description = "coCloud-server";

    private String termsOfServiceUrl = "http://127.0.0.1:${server.port}";

    private String contactName = "rubin";

    private String contactUrl = "https://agility6.site";

    private String contactEmail = "agility1013@gmail.com";

    private String version = "1.0";


}
