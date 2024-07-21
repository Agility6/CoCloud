package com.coCloud.server.modules.share.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * ClassName: ShareFileDownloadContext
 * Description: 分享文件下载上下文实体对象
 *
 * @Author agility6
 * @Create 2024/7/16 12:38
 * @Version: 1.0
 */
@Data
public class ShareFileDownloadContext implements Serializable {

    private static final long serialVersionUID = -7157812263956793589L;

    /**
     * 要下载的文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 相应实体
     */
    private HttpServletResponse response;
}
