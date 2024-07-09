package com.coCloud.server.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName: ShareTypeEnum
 * Description: 分享类型枚举类
 *
 * @Author agility6
 * @Create 2024/7/9 14:01
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum ShareTypeEnum {

    NEED_SHARE_CODE(0, "有提取码");

    private Integer code;

    private String desc;

}
