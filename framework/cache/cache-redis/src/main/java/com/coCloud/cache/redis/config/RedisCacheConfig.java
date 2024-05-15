package com.coCloud.cache.redis.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * ClassName: RedisCacheConfig
 * Description: Redis Cache配置类
 * 该缓存方案支持事务
 * 该缓存方案直接集成spring-boot-starter-data-redis，所以舍弃自定义配置，直接默认使用spring的配置
 *
 * @Author agility6
 * @Create 2024/5/10 20:52
 * @Version: 1.0
 */
@SpringBootConfiguration
@EnableCaching
@Slf4j
public class RedisCacheConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

        // // 创建 Jackson2JsonRedisSerializer 实例，用于序列化对象为 JSON 格式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        // 创建 StringRedisSerializer 实例，用于序列化字符串
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // 创建 RedisTemplate 实例
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // 创建 ObjectMapper 实例，用于配置 JSON 序列化方式
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置 RedisTemplate 的连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        // 设置键的序列化方式
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // 设置值的序列化方式
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        // 设置哈希键的序列化方式
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // 设置哈希值的序列化方式
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate; // 返回定制化的 RedisTemplate 实例

    }

    /**
     * 创建定制化的 RedisCacheManager 实例，用于定制化 Redis 的缓存管理器。
     *
     * @param redisConnectionFactory Redis 连接工厂
     * @return 定制化的 RedisCacheManager 实例
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        // 创建 Jackson2JsonRedisSerializer 实例，用于序列化对象为 JSON 格式
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        // 创建 ObjectMapper 实例，用于配置 JSON 序列化方式
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 创建 RedisCacheConfiguration 实例，用于配置 Redis 缓存的序列化方式
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer));

        // 创建 RedisCacheManager 实例，用于管理 Redis 缓存
        RedisCacheManager cacheManager = RedisCacheManager.builder(RedisCacheWriter.lockingRedisCacheWriter(redisConnectionFactory)).cacheDefaults(redisCacheConfiguration).transactionAware().build();

        // 打印日志，表示 Redis 缓存管理器加载成功
        log.info("Redis 缓存管理器加载成功！");
        return cacheManager; // 返回定制化的 RedisCacheManager 实例
    }
}
