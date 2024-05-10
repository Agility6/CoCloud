package com.coCloud.core.exception;

/**
 * ClassName: CoCloudFrameworkException
 * Description: 技术组件层面的异常对象
 *
 * @Author agility6
 * @Create 2024/5/10 15:44
 * @Version: 1.0
 */
public class CoCloudFrameworkException extends RuntimeException {

    public CoCloudFrameworkException(String message) {
        super(message);
    }
}
