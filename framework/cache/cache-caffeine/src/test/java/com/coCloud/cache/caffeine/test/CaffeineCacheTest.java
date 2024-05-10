package com.coCloud.cache.caffeine.test;

import cn.hutool.core.lang.Assert;
import com.coCloud.cache.caffeine.test.config.CaffeineCacheConfig;
import com.coCloud.cache.caffeine.test.instance.CacheAnnotationTester;
import com.coCloud.cache.core.constants.CacheConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ClassName: CaffeineCacheTest
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/10 20:36
 * @Version: 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = CaffeineCacheConfig.class)
public class CaffeineCacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CacheAnnotationTester cacheAnnotationTester;

    /**
     * 简单测试CacheManger的功能以及获取的Cache对象的功能
     * 手动添加数据到缓存中
     */
    @Test
    public void caffeineCacheManagerTest() {
        // 从manager中获取缓存数据
        Cache cache = cacheManager.getCache(CacheConstants.CO_CLOUD_CACHE_NAME);
        Assert.notNull(cache);
        // 添加数据到缓存中
        cache.put("name", "value");
        // 获取key为name的值
        String value = cache.get("name", String.class);
        Assert.isTrue("value".equals(value));
    }

    /**
     * 第一次根据注解添加缓存
     * 第二次缓存中存在从缓存中取数据
     */
    @Test
    public void caffeineCacheAnnotationTest() {
        for (int i = 0; i < 2; i++) {
            cacheAnnotationTester.testCacheable("coCloud");
        }
    }


}
