package com.coCloud.server.common.aspect;

import com.coCloud.cache.core.constants.CacheConstants;
import com.coCloud.core.response.R;
import com.coCloud.core.response.ResponseCode;
import com.coCloud.core.utils.JwtUtil;
import com.coCloud.server.common.annotation.LoginIgnore;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.user.constants.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * ClassName: CommonLoginAspect
 * Description: 统一的登录拦截校验切面逻辑实现类
 *
 * @Author agility6
 * @Create 2024/5/13 23:04
 * @Version: 1.0
 */
@Component
@Aspect
@Slf4j
public class CommonLoginAspect {

    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    private final static String POINT_CUT = "execution(* com.coCloud.server.modules.*.controller..*(..))";

    @Autowired
    private CacheManager cacheManager;

    /**
     * 切点模版方法
     */
    @Pointcut(value = POINT_CUT)
    public void loginAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * 1. 需要判断需不需要校验登录
     * 2. 校验登录信息：
     * - 获取token从请求头或者参数
     * - 从缓存中获取token，进行比对
     * - 解析token
     * - 解析的userId存入线程上下文，供下游使用
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    @Around("loginAuth()")
    public Object loginAuthAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 判断是否需要校验登录
        if (checkNeedCheckLoginInfo(proceedingJoinPoint)) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("成功拦截到请求，URI为：{}", requestURI);
            // 判断是否需要登录
            if (!checkAndSaveUserId(request)) {
                log.warn("成功拦截到请求，URI为：{}. 检测到用户未登录，将跳转至登录页面", requestURI);
                return R.fail(ResponseCode.NEED_LOGIN);
            }
            log.info("成功拦截到请求，URI为：{}，请求通过", requestURI);
        }
        return proceedingJoinPoint.proceed();
    }


    /* =============> private <============= */


    /**
     * 校验token并提取userId
     *
     * @param request
     * @return
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        // Header中获取accessToken
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            //  parameter中获取
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        // 如果依然不存在那么直接返回false
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }

        // 通过token获取userId
        Object userId = JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        // 如果userId不存在返回false重新登录
        if (Objects.isNull(userId)) {
            return false;
        }

        Cache cache = cacheManager.getCache(CacheConstants.CO_CLOUD_CACHE_NAME);
        // 缓存中获取token
        String redisAccessToken = cache.get(UserConstants.USER_LOGIN_PREFIX + userId, String.class);

        // 如果缓存中的token不存在那么返回false
        if (StringUtils.isBlank(redisAccessToken)) {
            return false;
        }

        // 传入的token和缓存中的一致返回true
        if (Objects.equals(accessToken, redisAccessToken)) {
            saveUserId(userId);
            return true;
        }

        return false;

    }

    /**
     * 保存用户ID到线程上下文中
     *
     * @param userId
     */
    private void saveUserId(Object userId) {
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
    }


    /**
     * 校验是否需要校验登录信息(检查方法是否有LoginIgnore注解)
     *
     * @param proceedingJoinPoint
     * @return true 需要校验登录信息 false不需要
     */
    private boolean checkNeedCheckLoginInfo(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }


}
