package com.coCloud.server.common.schedule.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.schedule.ScheduleTask;
import com.coCloud.server.common.event.log.ErrorLogEvent;
import com.coCloud.server.modules.file.entity.CoCloudFileChunk;
import com.coCloud.server.modules.file.service.IFileChunkService;
import com.coCloud.storage.engine.core.StorageEngine;
import com.coCloud.storage.engine.core.context.DeleteFileContext;
import io.jsonwebtoken.impl.crypto.RsaSignatureValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: CleanExpireChunkFileTask
 * Description: 过期分片清理任务
 *
 * @Author agility6
 * @Create 2024/6/6 22:30
 * @Version: 1.0
 */
@Component
@Slf4j
public class CleanExpireChunkFileTask implements ScheduleTask, ApplicationContextAware {

    private static final Long BATCH_SIZE = 500L;

    @Autowired
    private IFileChunkService iFileChunkService;

    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取定时任务的名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "ClearExpireChunkFileTask";
    }

    /**
     * 执行清理任务
     * <p>
     * 1. 滚动查询过期的文件分片
     * 2. 删除物理文件（委托文件存储引擎去实现）
     * 3. 删除过期文件分片的记录信息
     * 4. 重置上次查询的最大文件分片记录ID，继续滚动查询
     */
    @Override
    public void run() {
        log.info("{} start clean expire chunk file...", getName());

        List<CoCloudFileChunk> expireFileChunkRecords;
        Long scrollPointer = 1L;

        do {
            expireFileChunkRecords = scrollQueryExpireFileChunkRecords(scrollPointer);
            if (CollectionUtils.isNotEmpty(expireFileChunkRecords)) {
                deleteRealChunkFiles(expireFileChunkRecords);
                List<Long> idList = deleteChunkFileRecords(expireFileChunkRecords);
                scrollPointer = Collections.max(idList);
            }
        } while (CollectionUtils.isNotEmpty(expireFileChunkRecords));

        log.info("{} finish clean expire chunk file...", getName());
    }

    /**
     * 删除过期文件分片记录
     *
     * @param expireFileChunkRecords
     * @return
     */
    private List<Long> deleteChunkFileRecords(List<CoCloudFileChunk> expireFileChunkRecords) {
        List<Long> idList = expireFileChunkRecords.stream().map(CoCloudFileChunk::getId).collect(Collectors.toList());
        iFileChunkService.removeByIds(idList);
        return idList;
    }

    /**
     * 物理删除过期的文件分片文件实体
     *
     * @param expireFileChunkRecords
     */
    private void deleteRealChunkFiles(List<CoCloudFileChunk> expireFileChunkRecords) {
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<String> realPaths = expireFileChunkRecords.stream().map(CoCloudFileChunk::getRealPath).collect(Collectors.toList());
        deleteFileContext.setRealFilePathList(realPaths);
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            // 保存日志
            saveErrorLog(realPaths);
        }

    }

    private void saveErrorLog(List<String> realPaths) {
        ErrorLogEvent event = new ErrorLogEvent(this, "文件物理删除失败，请手动执行文件删除！文件路径为：" + JSON.toJSONString(realPaths), CoCloudConstants.ZERO_LONG);
        applicationContext.publishEvent(event);
    }

    /**
     * 滚动查询过期的文件分片记录
     *
     * @param scrollPointer
     * @return
     */
    private List<CoCloudFileChunk> scrollQueryExpireFileChunkRecords(Long scrollPointer) {
        QueryWrapper queryWrapper = Wrappers.query();
        // 添加查询条件：筛选过期时间小于等于当前时间的记录
        queryWrapper.le("expiration_time", new Date());
        // 添加查询条件：筛选ID大于等于传入的滚动指针（scrollPointer）的记录
        queryWrapper.ge("id", scrollPointer);
        // 添加限制条件：限制查询结果的数量为批量大小（BATCH_SIZE）
        queryWrapper.last(" limit " + BATCH_SIZE);
        return iFileChunkService.list(queryWrapper);
    }
}
