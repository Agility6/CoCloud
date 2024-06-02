package com.coCloud.server.common.event.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * ClassName: UserSearchEvent
 * Description: 用户搜索事件
 *
 * @Author agility6
 * @Create 2024/6/2 21:10
 * @Version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserSearchEvent extends ApplicationEvent {

    private String keyword;

    private Long userId;

    public UserSearchEvent(Object source, String keyword, Long userId) {
        super(source);
        this.keyword = keyword;
        this.userId = userId;
    }
}
