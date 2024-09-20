package com.coCloud.bloom.filter.local;

import com.coCloud.bloom.filter.core.BloomFilter;
import com.google.common.hash.Funnel;

/**
 * ClassName: LocalBloomFilter
 * Description: 本地实现的布隆过滤器
 *
 * @Author agility6
 * @Create 2024/8/3 15:18
 * @Version: 1.0
 */
public class LocalBloomFilter<T> implements BloomFilter<T> {

    private com.google.common.hash.BloomFilter delegate;

    /**
     * 数据类型通道
     */
    private Funnel funnel;

    /**
     * 数组的长度
     */
    private long expectedInsertions;

    /**
     * 误判率
     */
    private double fpp;

    public LocalBloomFilter(Funnel funnel, long expectedInsertions, double fpp) {
        this.funnel = funnel;
        this.expectedInsertions = expectedInsertions;
        this.fpp = fpp;
        this.delegate = com.google.common.hash.BloomFilter.create(funnel, expectedInsertions, fpp);
    }

    @Override
    public boolean put(T object) {
        return delegate.put(object);
    }

    @Override
    public boolean mightContain(T object) {
        return delegate.mightContain(object);
    }

    @Override
    public void clear() {
        this.delegate = com.google.common.hash.BloomFilter.create(funnel, expectedInsertions, fpp);
    }
}
