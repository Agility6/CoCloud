package com.coCloud.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName: MergeFlagEnum
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/25 15:56
 * @Version: 1.0
 */
@Getter
@AllArgsConstructor
public enum MergeFlagEnum {

    /**
     * 不需要合并
     */
    NOT_READY(0),
    /**
     * 需要合并
     */
    READY(1);

    private Integer code;

}
