package com.coCloud.cache.caffeine.test.instance;

import com.coCloud.cache.core.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * ClassName: CacheAnnotationTester
 * Description: Cache注解测试实体
 *
 * @Author agility6
 * @Create 2024/5/10 20:32
 * @Version: 1.0
 */
@Component
@Slf4j
public class CacheAnnotationTester {

    /**
     * cacheNames = CacheConstants.CO_CLOUD_CACHE_NAME: 指定缓存的名称，
     * key = "#name": 指定缓存的键，#name 是 SpEL（Spring Expression Language）表达式，表示方法参数 name 的值作为缓存的键。
     *
     * @param name
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.CO_CLOUD_CACHE_NAME, key = "#name", sync = true)
    public String testCacheable(String name) {
        log.info("call com.coCloud.cache.caffeine.test.instance.CacheAnnotationTester.testCacheable, param is {}", name);
        return new StringBuilder("hello ").append(name).toString();
    }
}
