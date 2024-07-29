package com.coCloud.server.modules.user.service.cache.keygenerator;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * ClassName: UserIdKeyGenerator
 * Description: 自定义缓存Key生成器
 *
 * @Author agility6
 * @Create 2024/7/28 23:16
 * @Version: 1.0
 */
@Component(value = "userIdKeyGenerator")
public class UserIdKeyGenerator implements KeyGenerator {

    private static final String USER_ID_PREFIX = "USER:ID";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder stringBuilder = new StringBuilder(USER_ID_PREFIX);
        if (params == null || params.length == 0) {
            return stringBuilder.toString();
        }

        Serializable id;
        for (Object param : params) {
            if (params instanceof Serializable) {
                id = (Serializable) param;
                stringBuilder.append(id);
                return stringBuilder.toString();
            }
        }

        stringBuilder.append(StringUtils.arrayToCommaDelimitedString(params));
        return stringBuilder.toString();
    }
}
