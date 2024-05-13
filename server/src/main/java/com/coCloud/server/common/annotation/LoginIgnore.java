package com.coCloud.server.common.annotation;

import java.lang.annotation.*;

/**
 * ClassName: LoginIgnore
 * Description: 该注解主要影响那些不需要登录的接口
 * 标注该注解的方式会自动屏蔽统一的登录拦截校验逻辑
 *
 * @Author agility6
 * @Create 2024/5/13 23:01
 * @Version: 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LoginIgnore {
}
