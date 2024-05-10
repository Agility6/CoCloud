package com.coCloud.cache.caffeine.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ClassName: CaffeineCacheProperties
 * Description: Caffeine Cache定义一配置属性类
 *
 * @Author agility6
 * @Create 2024/5/10 20:20
 * @Version: 1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "com.coCloud.cache.caffeine")
public class CaffeineCacheProperties {

    /**
     * 缓存初始容量
     * com.coCloud.cache.caffeine.init-cache-capacity
     */
    private Integer initCacheCapacity = 256;

    /**
     * 缓存最大容量，超过之后会按照recently or very often（最近最少）策略进行缓存剔除
     * com.coCloud.cache.caffeine.max-cache-capacity
     */
    private Long maxCacheCapacity = 10000L;

    /**
     * 是否允许空值null作为缓存的value
     * com.coCloud.cache.caffeine.allow-null-value
     */
    private Boolean allowNullValue = Boolean.TRUE;
}
