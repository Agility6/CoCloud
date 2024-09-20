package com.coCloud.server.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * ClassName: BloomFilterInterceptor
 * Description: 布隆过滤器拦截器顶级接口
 *
 * @Author agility6
 * @Create 2024/8/3 16:01
 * @Version: 1.0
 */
public interface BloomFilterInterceptor extends HandlerInterceptor {

    /**
     * 拦截器的名称
     *
     * @return
     */
    String getName();

    /**
     * 要拦截的URL的集合
     *
     * @return
     */
    String[] getPathPatterns();

    /**
     * 要排除拦截的URL的集合
     *
     * @return
     */
    String[] getExcludePatterns();
}
