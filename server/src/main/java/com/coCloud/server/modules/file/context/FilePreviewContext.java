package com.coCloud.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * ClassName: FilePreviewContext
 * Description: 文件预览的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/6/1 16:12
 * @Version: 1.0
 */
@Data
public class FilePreviewContext implements Serializable {

    private static final long serialVersionUID = 3962567611350007312L;

    /**
     *  文件ID
     */
    private Long fileId;

    /**
     * 请求响应对象
     */
    private HttpServletResponse response;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
