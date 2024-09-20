package com.coCloud.bloom.filter.local;

import com.coCloud.bloom.filter.core.BloomFilter;
import com.coCloud.bloom.filter.core.BloomFilterManager;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * ClassName: LocalBloomFilterManager
 * Description: 本地布隆过滤器的管理器
 *
 * @Author agility6
 * @Create 2024/8/3 15:23
 * @Version: 1.0
 */
@Component
public class LocalBloomFilterManager implements BloomFilterManager, InitializingBean {

    @Autowired
    private LocalBloomFilterConfig config;

    /**
     * 容器
     */
    private final Map<String, BloomFilter> bloomFilterContainer = Maps.newConcurrentMap();

    @Override
    public BloomFilter getFilter(String name) {
        return bloomFilterContainer.get(name);
    }

    @Override
    public Collection<String> getFilterName() {
        return bloomFilterContainer.keySet();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<LocalBloomFilterConfigItem> items = config.getItems();
        if (CollectionUtils.isNotEmpty(items)) {
            items.stream().forEach(item -> {
                        String funnelTypeName = item.getFunnelTypeName();
                        try {
                            FunnelType funnelType = FunnelType.valueOf(funnelTypeName);
                            if (Objects.nonNull(funnelType)) {
                                bloomFilterContainer.putIfAbsent(item.getName(), new LocalBloomFilter(funnelType.getFunnel(), item.getExpectedInsertions(), item.getFpp()));
                            }
                        } catch (Exception e) {

                        }
                    }
            );
        }
    }
}
