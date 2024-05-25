package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.utils.FileUtils;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.event.log.ErrorLogEvent;
import com.coCloud.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.coCloud.server.modules.file.context.FileSaveContext;
import com.coCloud.server.modules.file.context.QueryRealFileListContext;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.entity.CoCloudFileChunk;
import com.coCloud.server.modules.file.service.IFileChunkService;
import com.coCloud.server.modules.file.service.IFileService;
import com.coCloud.server.modules.file.mapper.CoCloudFileMapper;
import com.coCloud.storage.engine.core.StorageEngine;
import com.coCloud.storage.engine.core.context.DeleteFileContext;
import com.coCloud.storage.engine.core.context.MergeFileContext;
import com.coCloud.storage.engine.core.context.StoreFileContext;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author agility6
 * @description 针对表【co_cloud_file(物理文件信息表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:22:09
 */
@Service
public class FileServiceImpl extends ServiceImpl<CoCloudFileMapper, CoCloudFile> implements IFileService, ApplicationContextAware {

    @Autowired
    private StorageEngine storageEngine;

    private ApplicationContext applicationContext;

    @Autowired
    private IFileChunkService iFileChunkService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<CoCloudFile> getFileList(QueryRealFileListContext context) {
        // 获取userID
        Long userId = context.getUserId();
        // 获取identifier
        String identifier = context.getIdentifier();
        // 查询数据库
        LambdaQueryWrapper<CoCloudFile> queryWrapper = new LambdaQueryWrapper<>();
        // 如果userId不为null
        queryWrapper.eq(Objects.nonNull(userId), CoCloudFile::getCreateUser, userId);
        // 如果 identifier 不为 null 且不为空白字符串，
        queryWrapper.eq(StringUtils.isNotBlank(identifier), CoCloudFile::getIdentifier, identifier);
        return list(queryWrapper);

    }

    /**
     * 上传单文件并保存实体记录
     * <p>
     * 1. 上传单文件
     * 2. 保存实体记录
     *
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        // 上传单文件
        storeMultipartFile(context);
        // 保存实体记录
        CoCloudFile record = doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(), context.getIdentifier(), context.getUserId());
        context.setRecord(record);
    }

    /**
     * 合并物理文件并保存物理文件记录
     * <p>
     * 1. 委托文件存储引擎合并文件分片
     * 2. 保存物理文件记录
     *
     * @param context
     */
    @Override
    public void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context) {
        doMergeFileChunk(context);
        CoCloudFile record = doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(), context.getIdentifier(), context.getUserId());
        context.setRecord(record);
    }


    /* =============> private <============= */

    /**
     * 保存实体文件记录
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private CoCloudFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        // 生成CoCloudFile
        CoCloudFile record = assembleCoCloudFile(filename, realPath, totalSize, identifier, userId);
        // 数据库保存失败
        // 需要删除物理文件信息
        if (!save(record)) {
            try {
                // 删除物理文件的上下文试实体信息
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                // 删除物理位置
                storageEngine.delete(deleteFileContext);
            } catch (IOException e) {
                e.printStackTrace();
                // 发布删除物理文件失败
                ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "文件物理删除失败，请执行手动删除！文件路径: " + realPath, userId);
                applicationContext.publishEvent(errorLogEvent);
            }
        }

        return record;
    }


    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     *
     * @param context
     */
    private void storeMultipartFile(FileSaveContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            // 存储
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoCloudBusinessException("文件上传失败");
        }
    }

    /**
     * 拼装文件实体对象
     *
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private CoCloudFile assembleCoCloudFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        CoCloudFile record = new CoCloudFile();

        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtils.getFileSuffix(filename));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());

        return record;
    }

    /**
     * 委托文件存储引擎合并文件分片
     * <p>
     * 1. 查询文件分片的记录
     * 2. 根据文件分片的记录去合并物理文件
     * 3. 删除文件分片记录
     * 4. 封装合并文件的真实存储路径到上下文信息中
     *
     * @param context
     */
    private void doMergeFileChunk(FileChunkMergeAndSaveContext context) {
        // 查询数据库的分片信息
        QueryWrapper<CoCloudFileChunk> queryWrapper = Wrappers.query();
        // identifier
        queryWrapper.eq("identifier", context.getIdentifier());
        // create_user
        queryWrapper.eq("create_user", context.getUserId());
        // expiration_time 大于等于当前时间
        queryWrapper.ge("expiration_time", new Date());

        // 获取chunkRecordedList，交给iFileChunkService
        List<CoCloudFileChunk> chunkRecordedList = iFileChunkService.list(queryWrapper);

        // 判断是否存在
        if (CollectionUtils.isEmpty(chunkRecordedList)) {
            throw new CoCloudBusinessException("未找到分片记录");
        }

        // 从chunkRecordedList获取realPathList，用于合并
        List<String> realPathList = chunkRecordedList.stream()
                // 根据chunkNumber进行排序，在存储引擎中直接合并即可
                .sorted(Comparator.comparing(CoCloudFileChunk::getChunkNumber)).map(CoCloudFileChunk::getRealPath).collect(Collectors.toList());

        // 委托给存储引擎实现
        try {
            // 创建mergeFileContext
            MergeFileContext mergeFileContext = new MergeFileContext();
            // set属性
            mergeFileContext.setFilename(context.getFilename());
            mergeFileContext.setIdentifier(context.getIdentifier());
            mergeFileContext.setUserId(context.getUserId());
            mergeFileContext.setRealPathList(realPathList);
            // 交给存储引擎
            storageEngine.mergeFile(mergeFileContext);
            // 合并之后返回realPath，添加到context中
            context.setRealPath(mergeFileContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoCloudBusinessException("文件分片合并失败");
        }

        // 从chunkRecordedList获取ID，用于删除数据库分片文件
        List<Long> fileChunkRecordIdList = chunkRecordedList.stream().map(CoCloudFileChunk::getId).collect(Collectors.toList());

        iFileChunkService.removeByIds(fileChunkRecordIdList);
    }


}




