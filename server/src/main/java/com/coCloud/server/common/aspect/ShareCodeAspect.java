package com.coCloud.server.common.aspect;

import com.coCloud.core.response.R;
import com.coCloud.core.response.ResponseCode;
import com.coCloud.core.utils.JwtUtil;
import com.coCloud.server.common.utils.ShareIdUtil;
import com.coCloud.server.modules.share.constants.ShareConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * ClassName: ShareCodeAspect
 * Description: 统一的分享码校验切面逻辑实现类
 *
 * @Author agility6
 * @Create 2024/7/13 17:59
 * @Version: 1.0
 */
@Component
@Aspect
@Slf4j
public class ShareCodeAspect {

    /**
     * 登录认证参数名称
     */
    private static final String SHARE_CODE_AUTH_PARAM_NAME = "shareToken";

    /**
     * 请求头登录认证key
     */
    private static final String SHARE_CODE_AUTH_REQUEST_HEADER_NAME = "Share-Token";

    /**
     * 切点表达式
     */
    private final static String POINT_CUT = "@annotation(com.coCloud.server.common.annotation.NeedShareCode)";

    /**
     * 切点模板方法
     */
    @Pointcut(value = POINT_CUT)
    public void shareCodeAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * <p>
     * 1. 需要判断需不需要校验分享token信息
     * 2. 校验登录信息
     * a. 获取token，从请求头或参数
     * b. 解析token
     * c. 解析的shareId存入线程上下文，供下游使用
     *
     * @param proceedingJoinPoint
     * @return
     * @throws Throwable
     */
    public Object shareCodeAuthAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // 从RequestContextHolder获取当前线程请求相关上下文
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestURI = request.getRequestURI();
        log.info("成功拦截到请求，URI为：{}", requestURI);
        // 检查并且保存分享码
        if (!checkAndSaveShareId(request)) {
            log.warn("成功拦截到请求，URI为：{}. 检测到用户的分享码失效，将跳转至分享码校验页面", requestURI);
            return R.fail(ResponseCode.ACCESS_DENIED);
        }
        log.info("成功拦截到请求，URL为：{}，请求通过", requestURI);
        return proceedingJoinPoint.proceed();
    }

    /**
     * 检验token并提取shareId
     *
     * @param request
     * @return
     */
    private boolean checkAndSaveShareId(HttpServletRequest request) {
        // 获取shareToken
        String shareToken = request.getHeader(SHARE_CODE_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(shareToken)) {
            shareToken = request.getParameter(SHARE_CODE_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(shareToken)) {
            return false;
        }

        Object shareId = JwtUtil.analyzeToken(shareToken, ShareConstants.SHARE_ID);
        if (Objects.isNull(shareId)) {
            return false;
        }
        saveShareId(shareId);
        return true;
    }

    /**
     * 保存分享ID到线程上下文中
     *
     * @param shareId
     */
    private void saveShareId(Object shareId) {
        ShareIdUtil.set(Long.valueOf(String.valueOf(shareId)));
    }
}
