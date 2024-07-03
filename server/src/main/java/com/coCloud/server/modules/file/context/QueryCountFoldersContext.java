package com.coCloud.server.modules.file.context;

import lombok.Data;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;

import java.io.Serializable;

/**
 * ClassName: QueryCountFoldersContext
 * Description: 查询文件重名的数量
 *
 * @Author agility6
 * @Create 2024/7/3 14:58
 * @Version: 1.0
 */
@Data
public class QueryCountFoldersContext implements Serializable {

    private static final long serialVersionUID = -2417901120941721980L;

    /**
     * 父文件ID
     */
    private Long parentId;

    /**
     * 是否是文件夹 （0 否 1 是）
     */
    private Integer folderFlag;

    /**
     * 用户Id信息
     */
    private Long userId;

    /**
     * 文件的删除标识
     */
    private Integer delFlag;

    /**
     * 文件名称
     */
    private String filename;

}
