package com.coCloud.server.common.annotation;

import java.lang.annotation.*;

/**
 * ClassName: NeedShareCode
 * Description: 该注解主要影响需要分享码校验的接口
 *
 * @Author agility6
 * @Create 2024/7/13 13:37
 * @Version: 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NeedShareCode {
}
