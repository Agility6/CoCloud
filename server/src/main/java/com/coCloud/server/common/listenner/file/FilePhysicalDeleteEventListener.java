package com.coCloud.server.common.listenner.file;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.server.common.event.file.FilePhysicalDeleteEvent;
import com.coCloud.server.common.event.log.ErrorLogEvent;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.FolderFlagEnum;
import com.coCloud.server.modules.file.service.IFileService;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.storage.engine.core.StorageEngine;
import com.coCloud.storage.engine.core.context.DeleteFileContext;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassName: FilePhysicalDeleteEventListener
 * Description: 文件物理删除监听器
 *
 * @Author agility6
 * @Create 2024/7/7 20:59
 * @Version: 1.0
 */
@Component
public class FilePhysicalDeleteEventListener implements ApplicationContextAware {

    @Autowired
    private IFileService iFileService;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 监听文件物理删除事件执行器
     * 该执行器是一个资源释放器，释放被物理删除的文件列表中关联的实体文件记录
     * <p>
     * 1. 查询所有无引用的实体文件记录
     * 2. 删除记录
     * 3. 物理清除文件（委托文件存储引擎）
     *
     * @param event
     */
    @EventListener(classes = FilePhysicalDeleteEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void physicalDeleteFile(FilePhysicalDeleteEvent event) {
        // 从event中获取所有要删除的文件信息
        List<CoCloudUserFile> allRecords = event.getAllRecords();
        // 判空
        if (CollectionUtils.isEmpty(allRecords)) {
            return;
        }

        // 执行findAllUnusedRealFileIdList返回没有被引用的真实文件ID集合
        List<Long> realFileIdList = findAllUnusedRealFileIdList(allRecords);

        // 通过realFileIdList查询出CoCloudFile的Records
        List<CoCloudFile> realFileRecords = iFileService.listByIds(realFileIdList);
        // 判空realFileRecords
        if (CollectionUtils.isEmpty(realFileRecords)) {
            return;
        }

        // 删除数据库中的realFileRecords
        if (!iFileService.removeByIds(realFileIdList)) {
            // 异常需要发布一个ErrorLogEvent
            applicationContext.publishEvent(new ErrorLogEvent(this, "实体文件记录：" + JSON.toJSONString(realFileIdList) + "， 物理删除失败，请执行手动删除", CoCloudConstants.ZERO_LONG));
            return;
        }

        // 删除物理位置上的文件
        physicalDeleteFileByStorageEngine(realFileRecords);
    }


    /* =============> private <============= */

    /**
     * 查询所有没有被引用的真实文件记录ID集合
     *
     * @param allRecords
     * @return
     */
    private List<Long> findAllUnusedRealFileIdList(List<CoCloudUserFile> allRecords) {
        List<Long> realFileIdList = allRecords.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.NO.getCode())) // 不是文件夹类型的
                .filter(this::isUnused).map(CoCloudUserFile::getRealFileId).collect(Collectors.toList());

        return realFileIdList;
    }

    /**
     * 校验文件的真实文件ID是不是没有被引用了
     *
     * @param record
     * @return
     */
    private boolean isUnused(CoCloudUserFile record) {
        // 查询是否有real_file_id和当前record的RealFileId相等
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("real_file_id", record.getRealFileId());
        return iUserFileService.count(queryWrapper) == CoCloudConstants.ZERO_INT.intValue();
    }

    /**
     * 委托文件存储引擎执行物理文件的删除
     *
     * @param realFileRecords
     */
    private void physicalDeleteFileByStorageEngine(List<CoCloudFile> realFileRecords) {
        // 获取realPathList
        List<String> realPathList = realFileRecords.stream().map(CoCloudFile::getRealPath).collect(Collectors.toList());
        // 创建DeleteContext，注意是StorageEngine的
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setRealFilePathList(realPathList);

        // 执行删除动作
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            // 出现异常需要发布异常事件
            applicationContext.publishEvent(new ErrorLogEvent(this, "实体文件记录：" + JSON.toJSONString(realPathList) + "， 物理删除失败，请执行手动删除", CoCloudConstants.ZERO_LONG));
        }
    }


}
