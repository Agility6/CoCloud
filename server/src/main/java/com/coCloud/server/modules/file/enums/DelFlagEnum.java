package com.coCloud.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName: DelFlagEnum
 * Description: 文件删除标识枚举类
 *
 * @Author agility6
 * @Create 2024/5/13 14:31
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum DelFlagEnum {

    /**
     * 未删除
     */
    NO(0),
    /**
     * 已删除
     */
    YES(1);

    private Integer code;
}
