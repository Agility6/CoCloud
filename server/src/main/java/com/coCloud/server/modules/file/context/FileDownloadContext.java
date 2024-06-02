package com.coCloud.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * ClassName: FileDownloadContext
 * Description: 文件下载的上下文实体对象
 *
 * @Author agility6
 * @Create 2024/5/31 21:07
 * @Version: 1.0
 */
@Data
public class FileDownloadContext implements Serializable {

    private static final long serialVersionUID = -3414007757082720097L;

    /**
     * 文件ID
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
