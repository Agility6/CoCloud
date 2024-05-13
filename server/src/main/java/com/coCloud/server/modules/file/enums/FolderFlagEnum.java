package com.coCloud.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ClassName: FolderFlagEnum
 * Description: 文件表示枚举类
 *
 * @Author agility6
 * @Create 2024/5/13 14:22
 * @Version: 1.0
 */
@AllArgsConstructor
@Getter
public enum FolderFlagEnum {

    /**
     * 非文件夹
     */
    NO(0),
    /**
     * 是文件夹
     */
    YES(1);

    private Integer code;
}
