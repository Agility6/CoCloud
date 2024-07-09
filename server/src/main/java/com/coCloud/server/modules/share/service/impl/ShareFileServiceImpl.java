package com.coCloud.server.modules.share.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.modules.share.context.SaveShareFilesContext;
import com.coCloud.server.modules.share.entity.CoCloudShareFile;
import com.coCloud.server.modules.share.service.IShareFileService;
import com.coCloud.server.modules.share.mapper.CoCloudShareFileMapper;
import io.micrometer.core.instrument.Meter;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author agility6
 * @description 针对表【co_cloud_share_file(用户分享文件表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:23:23
 */
@Service
public class ShareFileServiceImpl extends ServiceImpl<CoCloudShareFileMapper, CoCloudShareFile> implements IShareFileService {

    /**
     * 保存分享的文件的对应关系
     * <p>
     * 1. 将context中的shareFileIdList遍历设置属性
     *
     * @param context
     */
    @Override
    public void saveShareFiles(SaveShareFilesContext context) {

        Long shareId = context.getShareId();
        Long userId = context.getUserId();
        List<Long> shareFileIdList = context.getShareFileIdList();

        List<CoCloudShareFile> records = Lists.newArrayList();

        for (Long shareFileId : shareFileIdList) {
            CoCloudShareFile record = new CoCloudShareFile();
            record.setId(IdUtil.get());
            record.setShareId(shareId);
            record.setFileId(shareFileId);
            record.setCreateUser(userId);
            record.setCreateTime(new Date());
            records.add(record);
        }

        if (!saveBatch(records)) {
            throw new CoCloudBusinessException("保存文件分享关联关系失败");
        }
    }
}




