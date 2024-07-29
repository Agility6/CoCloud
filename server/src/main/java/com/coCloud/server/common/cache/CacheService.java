package com.coCloud.server.common.cache;

import java.io.Serializable;

/**
 * ClassName: CacheService
 * Description: 支持业务缓存的顶级Service接口
 *
 * @Author agility6
 * @Create 2024/7/28 17:24
 * @Version: 1.0
 */
public interface CacheService<V> {

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    V getById(Serializable id);

    /**
     * 根据ID来更新缓存信息
     *
     * @param id
     * @param entity
     * @return
     */
    boolean updateById(Serializable id, V entity);

    /**
     * 根据ID来删除缓存信息
     *
     * @param id
     * @return
     */
    boolean removeById(Serializable id);
}
