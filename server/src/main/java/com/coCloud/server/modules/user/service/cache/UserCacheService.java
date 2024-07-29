package com.coCloud.server.modules.user.service.cache;

import com.coCloud.cache.core.constants.CacheConstants;
import com.coCloud.server.common.cache.AnnotationCacheService;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.mapper.CoCloudUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * ClassName: UserCacheService
 * Description: 用户模块缓存业务处理类
 *
 * @Author agility6
 * @Create 2024/7/28 23:03
 * @Version: 1.0
 */
@Component(value = "userAnnotationCacheService")
public class UserCacheService implements AnnotationCacheService<CoCloudUser> {

    @Autowired
    private CoCloudUserMapper mapper;

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.CO_CLOUD_CACHE_NAME, keyGenerator = "userIdKeyGenerator", sync = true)
    @Override
    public CoCloudUser getById(Serializable id) {
        return mapper.selectById(id);
    }

    /**
     * 根据ID更新实体
     *
     * @param id
     * @param entity
     * @return
     */
    @CachePut(cacheNames = CacheConstants.CO_CLOUD_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, CoCloudUser entity) {
        return mapper.updateById(entity) == 1;
    }

    /**
     * 根据ID删除实体
     *
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = CacheConstants.CO_CLOUD_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return mapper.deleteById(id) == 1;
    }
}
