package com.coCloud.server.modules.file.context;

import com.sun.org.apache.xml.internal.serializer.SerializerTrace;
import lombok.Data;

import java.io.Serializable;

/**
 * ClassName: QueryRealFileListContext
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/19 23:37
 * @Version: 1.0
 */
@Data
public class QueryRealFileListContext implements Serializable {

    private static final long serialVersionUID = 5724654288087022552L;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 文件的唯一标识
     */
    private String identifier;
}
