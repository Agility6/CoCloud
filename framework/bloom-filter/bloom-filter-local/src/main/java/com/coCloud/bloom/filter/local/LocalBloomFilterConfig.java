package com.coCloud.bloom.filter.local;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClassName: LocalBloomFilterConfig
 * Description:
 *
 * @Author agility6
 * @Create 2024/8/3 14:55
 * @Version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "com.co-cloud.bloom.filter.local")
@Data
public class LocalBloomFilterConfig {

    private List<LocalBloomFilterConfigItem> items;

}
