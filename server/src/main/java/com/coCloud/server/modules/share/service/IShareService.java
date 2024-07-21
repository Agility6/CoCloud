package com.coCloud.server.modules.share.service;

import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.share.context.*;
import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlListVO;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlVO;
import com.coCloud.server.modules.share.vo.ShareDetailVO;
import com.coCloud.server.modules.share.vo.ShareSimpleDetailVO;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.util.List;

/**
 * @author agility6
 * @description 针对表【co_cloud_share(用户分享表)】的数据库操作Service
 * @createDate 2024-05-10 19:23:23
 */
public interface IShareService extends IService<CoCloudShare> {

    /**
     * 创建分享链接
     *
     * @param context
     * @return
     */
    CoCloudShareUrlVO create(CreateShareUrlContext context);

    /**
     * 查询用户分享列表
     *
     * @param context
     * @return
     */
    List<CoCloudShareUrlListVO> getShares(QueryShareListContext context);

    /**
     * 取消分享链接
     *
     * @param context
     */
    void cancelShare(CancelShareContext context);

    /**
     * 校验分享码
     *
     * @param context
     * @return
     */
    String checkShareCode(CheckShareCodeContext context);

    /**
     * 查询分享的详情
     *
     * @param context
     * @return
     */
    ShareDetailVO detail(QueryShareDetailContext context);

    /**
     * 查询分享的简单详情
     *
     * @param context
     * @return
     */
    ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context);

    /**
     * 获取下一级的文件列表
     *
     * @param context
     * @return
     */
    List<CoCloudUserFileVO> fileList(QueryChildFileListContext context);

    /**
     * 转存至我的网盘
     *
     * @param context
     */
    void saveFiles(ShareSaveContext context);

    /**
     * 分享的文件下载
     *
     * @param context
     */
    void download(ShareFileDownloadContext context);
}
