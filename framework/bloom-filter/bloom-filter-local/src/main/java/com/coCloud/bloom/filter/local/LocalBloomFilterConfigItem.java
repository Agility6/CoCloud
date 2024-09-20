package com.coCloud.bloom.filter.local;

import lombok.Data;

/**
 * ClassName: LocalBloomFilterConfigItem
 * Description: 本地的布隆过滤器单体配置类
 *
 * @Author agility6
 * @Create 2024/8/3 15:11
 * @Version: 1.0
 */
@Data
public class LocalBloomFilterConfigItem {

    /**
     * 布隆过滤器的名称
     */
    private String name;

    /**
     * 数据通道的名称
     */
    private String funnelTypeName = FunnelType.LONG.name();

    /**
     *  数组的长度
     */
    private long expectedInsertions = 10000000L;

    /**
     * 误判率
     */
    private double fpp = 0.01D;
}
