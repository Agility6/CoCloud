package com.coCloud.cache.redis.test;

import com.coCloud.cache.redis.test.config.RedisCacheConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: RedisCacheTest
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/10 21:11
 * @Version: 1.0
 */
@SpringBootTest(classes = RedisCacheConfig.class)
@SpringBootApplication
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisCacheTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisTemplate() {
        // 存储一个对象到Redis中
        String key = "test:map";
        Map<String, String> map = new HashMap<>();
        map.put("name", "value");
        redisTemplate.opsForValue().set(key, map);

        // 从Redis中获取存储的对象
        Map<String, String> retrievedMap = (Map<String, String>) redisTemplate.opsForValue().get(key);

        // 断言存储和检索的对象是否相同
        assert retrievedMap != null;
        assert retrievedMap.get("name").equals(map.get("name"));
    }
}
