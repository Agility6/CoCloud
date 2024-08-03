package com.coCloud.server.common.cache;


import org.springframework.cache.Cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ClassName: ManualCacheService
 * Description: 手动缓存处理Service顶级接口
 *
 * @Author agility6
 * @Create 2024/7/28 17:38
 * @Version: 1.0
 */
public interface ManualCacheService<V> extends CacheService<V> {

    /**
     * 根据ID集合查询实体记录列表
     *
     * @param ids
     * @return
     */
    List<V> getByIds(Collection<? extends Serializable> ids);

    /**
     * 批量更新实体记录
     *
     * @param entityMap
     * @return
     */
    boolean updateByIds(Map<? extends Serializable, V> entityMap);

    /**
     * 批量删除实体记录
     *
     * @param ids
     * @return
     */
    boolean removeByIds(Collection<? extends Serializable> ids);

    /**
     * 获取缓存Key的模版信息
     *
     * @return
     */
    String getKeyFormat();

    /**
     * 获取缓存对象实体
     *
     * @return
     */
    Cache getCache();
}