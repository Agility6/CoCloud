package com.coCloud.web.log;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 打印HTTP调用日志过滤器，使用者可以按需将其注入到过滤器容器中使用
 * 这里只提供基础的过滤实现
 */
@WebFilter(filterName = "httpLogFilter")
@Slf4j
@Order(Integer.MAX_VALUE)
public class HttpLogFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 创建一个计时器StopWatch对象，并启动计时器，用于记录HTTP请求的处理时间
        StopWatch stopWatch = StopWatch.createStarted();
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        // 调用FilterChain的doFilter方法，将包装后的请求和响应对象传递给下一个过滤器或Servlet进行处理。
        filterChain.doFilter(requestWrapper, responseWrapper);
        // 使用HttpLogEntityBuilder类的build方法，传入请求、响应和计时器对象，构建一个HttpLogEntity对象，用于表示HTTP请求的日志信息。
        HttpLogEntity httpLogEntity = HttpLogEntityBuilder.build(requestWrapper, responseWrapper, stopWatch);
        httpLogEntity.print();
        // 将响应内容从响应包装器中复制到原始的HttpServletResponse对象中，以确保响应内容正常返回给客户端。
        responseWrapper.copyBodyToResponse();
    }

}
