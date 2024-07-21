package com.coCloud.server.common.listenner.share;

import com.coCloud.server.common.event.file.DeleteFileEvent;
import com.coCloud.server.common.event.file.FileRestoreEvent;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.share.service.IShareFileService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassName: ShareStatusChangeListener
 * Description: 监听文件状态变更导致状态变更的处理器
 *
 * @Author agility6
 * @Create 2024/7/21 14:55
 * @Version: 1.0
 */
@Component
public class ShareStatusChangeListener {

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private IShareFileService iShareFileService;


    /**
     * 监听文件被删除后，刷新所有受影响的分享的状态
     * @param event
     */
    @EventListener(DeleteFileEvent.class)
    public void changeShare2FileDeleted(DeleteFileEvent event) {
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }

        List<CoCloudUserFile> records = iUserFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = records.stream()
                .filter(record -> Objects.equals(DelFlagEnum.NO.getCode(), record.getDelFlag()))
                .map(CoCloudUserFile::getFileId)
                .collect(Collectors.toList());

        // fileIdList需要重新添加到allAvailableFileIdList中
        allAvailableFileIdList.addAll(fileIdList);
        iShareFileService.refreshShareStatus(allAvailableFileIdList);


    }

    @EventListener(FileRestoreEvent.class)
    public void changeShare2Normal(FileRestoreEvent event) {

    }

}
