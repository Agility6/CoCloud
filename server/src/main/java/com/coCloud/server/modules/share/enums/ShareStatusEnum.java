package com.coCloud.server.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName: ShareStatusEnum
 * Description: 分享状态枚举类
 *
 * @Author agility6
 * @Create 2024/7/9 14:09
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum ShareStatusEnum {

    NORMAL(0, "正常状态"),
    FILE_DELETED(1, "有文件被删除");

    private Integer code;

    private String desc;
}
