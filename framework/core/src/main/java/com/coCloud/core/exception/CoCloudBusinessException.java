package com.coCloud.core.exception;

import com.coCloud.core.response.ResponseCode;
import lombok.Data;

/**
 * ClassName: CoCloudBusinessException
 * Description: 自定义全局业务异常类
 *
 * @Author agility6
 * @Create 2024/5/10 11:36
 * @Version: 1.0
 */
@Data
public class CoCloudBusinessException extends RuntimeException {
    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    public CoCloudBusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getDesc();
    }

    public CoCloudBusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public CoCloudBusinessException(String message) {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = message;
    }

    public CoCloudBusinessException() {
        this.code = ResponseCode.ERROR_PARAM.getCode();
        this.message = ResponseCode.ERROR_PARAM.getDesc();
    }
}
