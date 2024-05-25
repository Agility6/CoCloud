package com.coCloud.server.modules.file.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.config.CoCloudServerConfig;
import com.coCloud.server.modules.file.context.FileChunkSaveContext;
import com.coCloud.server.modules.file.converter.FileConverter;
import com.coCloud.server.modules.file.entity.CoCloudFileChunk;
import com.coCloud.server.modules.file.enums.MergeFlagEnum;
import com.coCloud.server.modules.file.service.IFileChunkService;
import com.coCloud.server.modules.file.mapper.CoCloudFileChunkMapper;
import com.coCloud.server.modules.file.vo.FileChunkUploadVO;
import com.coCloud.storage.engine.core.StorageEngine;
import com.coCloud.storage.engine.core.context.StoreFileChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * @author agility6
 * @description 针对表【co_cloud_file_chunk(文件分片信息表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:22:09
 */
@Service
public class FileChunkServiceImpl extends ServiceImpl<CoCloudFileChunkMapper, CoCloudFileChunk> implements IFileChunkService {

    @Autowired
    private CoCloudServerConfig config;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private StorageEngine storageEngine;

    /**
     * 文件分片保存
     * <p>
     * 1. 保存文件分片和记录
     * 2. 判断文件分片是否全部上传完成
     * 因为上传是使用到了多线程，那么在该方法下只允许一个线程进行操作，确保数据一致性
     *
     * @param context
     * @return
     */
    @Override
    public synchronized void saveChunkFile(FileChunkSaveContext context) {
        doSaveChunkFile(context);
        doJudgeMergeFile(context);
    }

    /* =============> private <============= */

    /**
     * 判断是否所有的分片均没上传完成
     *
     * @param context
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        // 查询数据库
        QueryWrapper queryWrapper = Wrappers.query();
        // identifier
        queryWrapper.eq("identifier", context.getIdentifier());
        queryWrapper.eq("create_user", context.getUserId());
        // 获取count
        int count = count(queryWrapper);
        // count与context.totalChunks比较是否分片均完成上传了
        if (count == context.getTotalChunks().intValue()) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 执行文件分片上传保存的操作
     * <p>
     * 1. 委托文件存储引擎存储文件分片
     * 2. 保存文件分片记录
     *
     * @param context
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        // 委托文件存储引擎存储文件分片
        doStoreFileChunk(context);
        // 保存文件分片记录
        doSaveRecord(context);
    }

    /**
     * 保存文件分片记录
     *
     * @param context
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        // 创建FileChunk
        CoCloudFileChunk record = new CoCloudFileChunk();
        // set属性
        record.setId(IdUtil.get());
        record.setIdentifier(context.getIdentifier());
        record.setRealPath(context.getRealPath());
        record.setChunkNumber(context.getChunkNumber());
        record.setExpirationTime(DateUtil.offsetDay(new Date(), config.getChunkFileExpirationDays()));
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());

        // 保存到数据中
        if (!save(record)) {
            throw new CoCloudBusinessException("文件分片长传失败");
        }
    }

    /**
     * 委托文件存储引擎保存文件分片
     *
     * @param context
     */
    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            // 将context转化为StoreFileChunkContext
            StoreFileChunkContext storeFileChunkContext = fileConverter.fileChunkSaveContext2StoreFileChunkContext(context);
            // 注入InputStream
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            // 委托存储引擎保存
            storageEngine.storeChunk(storeFileChunkContext);
            // 将realPath注入context中
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoCloudBusinessException("文件分片上传失败");
        }
    }
}




