package com.coCloud.bloom.filter.local;

import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

/**
 * ClassName: FunnelType
 * Description: 数据类型通道枚举类
 *
 * @Author agility6
 * @Create 2024/8/3 14:53
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum FunnelType {

    /**
     * long类型的数据通道
     */
    LONG(Funnels.longFunnel()),
    /**
     * int类型的数据通道
     */
    INTEGER(Funnels.integerFunnel()),
    /**
     * 字符串类型的数据通道
     */
    STRING(Funnels.stringFunnel(StandardCharsets.UTF_8));

    private Funnel funnel;
}
