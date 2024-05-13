package com.coCloud.server.common.utils;

import com.coCloud.core.constants.CoCloudConstants;

import java.util.Objects;

/**
 * ClassName: UserIdUtil
 * Description: 用户ID存储工具类
 *
 * @Author agility6
 * @Create 2024/5/13 21:48
 * @Version: 1.0
 */

public class UserIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     * @param userId
     */
    public static void set(Long userId) {
        threadLocal.set(userId);
    }


    /**
     * 获取当前线程的用户ID
     * @return
     */
    public static Long get() {
        Long userId = threadLocal.get();
        if (Objects.isNull(userId)) {
            return CoCloudConstants.ZERO_LONG;
        } else {
            return userId;
        }
    }

}
