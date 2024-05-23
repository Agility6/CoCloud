package com.coCloud.server.common.event.log;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * ClassName: ErrorLogEvent
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/23 0:36
 * @Version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ErrorLogEvent extends ApplicationEvent {

    /**
     * 错误日志的内容
     */
    private String errorMsg;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    public ErrorLogEvent(Object source, String errorMsg, Long userId) {
        super(source);
        this.errorMsg = errorMsg;
        this.userId = userId;
    }
}
