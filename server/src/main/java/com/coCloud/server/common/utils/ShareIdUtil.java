package com.coCloud.server.common.utils;

import com.coCloud.core.constants.CoCloudConstants;

import java.util.Objects;

/**
 * ClassName: ShareIdUtil
 * Description: 分享ID存储工具类
 *
 * @Author agility6
 * @Create 2024/7/13 18:19
 * @Version: 1.0
 */
public class ShareIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的分享ID
     *
     * @param shareId
     */
    public static void set(Long shareId) {
        threadLocal.set(shareId);
    }

    /**
     * 获取当前线程的分享ID
     *
     * @return
     */
    public static Long get() {
        Long shareId = threadLocal.get();
        if (Objects.isNull(shareId)) {
            return CoCloudConstants.ZERO_LONG;
        }
        return shareId;
    }

}
