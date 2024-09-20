package com.coCloud.bloom.filter.core;

/**
 * ClassName: BloomFilter
 * Description: 布隆过滤器的顶级接口
 *
 * @Author agility6
 * @Create 2024/8/3 14:46
 * @Version: 1.0
 */
public interface BloomFilter<T> {

    /**
     * 放入元素
     *
     * @param object
     * @return
     */
    boolean put(T object);

    /**
     * 判断元素是否可能存在
     *
     * @param object
     * @return
     */
    boolean mightContain(T object);

    /**
     * 清空布隆过滤器
     */
    void clear();

}
