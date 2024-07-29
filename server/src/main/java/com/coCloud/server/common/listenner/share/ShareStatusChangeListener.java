package com.coCloud.server.common.listenner.share;

import com.coCloud.server.common.event.file.DeleteFileEvent;
import com.coCloud.server.common.event.file.FileRestoreEvent;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.share.service.IShareFileService;
import com.coCloud.server.modules.share.service.IShareService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
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
    private IShareService iShareService;


    /**
     * 监听文件被删除后，刷新所有受影响的分享的状态
     *
     * @param event
     */
    @Async(value = "eventListenerTaskExecutor")
    @EventListener(DeleteFileEvent.class)
    public void changeShare2FileDeleted(DeleteFileEvent event) {
        // 删除的文件列表
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }

        // 查询文件的所有子文件信息（查询的结果也包括被删除标识的文件信息）
        List<CoCloudUserFile> records = iUserFileService.findAllFileRecordsByFileIdList(fileIdList);
        //  过滤被删除的
        List<Long> allAvailableFileIdList = records.stream()
                .filter(record -> Objects.equals(DelFlagEnum.NO.getCode(), record.getDelFlag()))
                .map(CoCloudUserFile::getFileId)
                .collect(Collectors.toList());

        // fileIdList（被监听删除的文件列表）需要重新添加到allAvailableFileIdList中
        allAvailableFileIdList.addAll(fileIdList);
        iShareService.refreshShareStatus(allAvailableFileIdList);
    }

    /**
     * 监听文件被还原后，刷新所有受影响的分享的状态
     *
     * @param event
     */
    @Async(value = "eventListenerTaskExecutor")
    @EventListener(FileRestoreEvent.class)
    public void changeShare2Normal(FileRestoreEvent event) {
        List<Long> fileIdList = event.getFileIdList();
        if (CollectionUtils.isEmpty(fileIdList)) {
            return;
        }
        List<CoCloudUserFile> allRecords = iUserFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> Objects.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(CoCloudUserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        iShareService.refreshShareStatus(allAvailableFileIdList);
    }

}
