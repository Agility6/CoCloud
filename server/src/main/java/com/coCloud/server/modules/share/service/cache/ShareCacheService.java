package com.coCloud.server.modules.share.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coCloud.server.common.cache.AbstractManualCacheService;
import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.coCloud.server.modules.share.mapper.CoCloudShareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName: ShareCacheService
 * Description: 手动缓存实现分享业务的查询等操作
 *
 * @Author agility6
 * @Create 2024/7/28 23:00
 * @Version: 1.0
 */
@Component(value = "shareManualCacheService")
public class ShareCacheService extends AbstractManualCacheService<CoCloudShare> {

    @Autowired
    private CoCloudShareMapper mapper;

    @Override
    protected BaseMapper<CoCloudShare> getBaseMapper() {
        return mapper;
    }

    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
