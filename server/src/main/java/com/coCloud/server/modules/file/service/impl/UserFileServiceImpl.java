package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.CreateFolderContext;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.enums.FolderFlagEnum;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.mapper.CoCloudUserFileMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author agility6
 * @description 针对表【co_cloud_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:22:09
 */
@Service(value = "fileService")
public class UserFileServiceImpl extends ServiceImpl<CoCloudUserFileMapper, CoCloudUserFile> implements IUserFileService {

    /**
     * 创建文件夹信息
     *
     * @param createFolderContext
     * @return
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getUserId(), createFolderContext.getFolderName(), FolderFlagEnum.YES, null, null, createFolderContext.getUserId(), null);
    }

    /* =============> private <============= */

    /**
     * 保存用户文件的映射记录
     *
     * @param parentId
     * @param filename
     * @param folderFlagEnum
     * @param fileType       文件类型（1 普通文件 2 压缩文件 3 excel 4 word 5 pdf 6 txt 7 图片 8 音频 9 视频 10 ppt 11 源码文件 12 csv）
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @return
     */
    private Long saveUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        CoCloudUserFile entity = assembleCoCloudUserFile(parentId, userId, filename, folderFlagEnum, fileType, realFileId, fileSizeDesc);
        if (!save(entity)) {
            throw new CoCloudBusinessException("报文件信息失败");
        }
        return entity.getUserId();
    }

    /**
     * 用户文件映射关系实体转化
     * 1. 构建并且填充实体
     * 2. 处理文件命名一致的问题
     *
     * @param parentId
     * @param userId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param fileSizeDesc
     * @return
     */
    private CoCloudUserFile assembleCoCloudUserFile(Long parentId, Long userId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, String fileSizeDesc) {
        CoCloudUserFile entity = new CoCloudUserFile();
        entity.setFileId(IdUtil.get());
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFilename(filename);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileType(fileType);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setCreateUser(userId);
        entity.setCreateTime(new Date());
        entity.setUpdateUser(userId);
        entity.setUpdateTime(new Date());

        // 处理文件夹相同命名
        handleDuplicateFilename(entity);

        return entity;
    }

    /**
     * 处理用户重复名称
     * 如果同一文件下面有文件名称重复
     * b --> a、a(1)
     *
     * @param entity
     */
    private void handleDuplicateFilename(CoCloudUserFile entity) {

        // 获取当前文件名称
        String filename = entity.getFilename(), newFilenameWithoutSuffix, // 文件没有后缀
                newFilenameSuffix; // 文件有后缀

        // 寻在文件"."的位置
        int newFilenamePointPosition = filename.lastIndexOf(CoCloudConstants.POINT_STR);

        if (newFilenamePointPosition == CoCloudConstants.MINUS_ONE_INT) { // 当前文件没有后缀
            newFilenameWithoutSuffix = filename;
            // 后缀为空
            newFilenameSuffix = StringUtils.EMPTY;
        } else { // 需要进行文件名截取
            // 获取没有没有后缀部分
            newFilenameWithoutSuffix = filename.substring(CoCloudConstants.ZERO_INT, newFilenamePointPosition);
            // 获取有后缀部分
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, StringUtils.EMPTY);
        }

        // 判断该目录是否有重复名称
        int count = getDuplicateFilename(entity, newFilenameWithoutSuffix);

        // 不存在重复
        if (count == 0) {
            return;
        }
        String newFilename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);
        entity.setFilename(newFilename);
    }

    /**
     * 拼装新文件名称
     * 拼装规则参考操作系统重复名称的重命名规范
     *
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @return
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, int count, String newFilenameSuffix) {
        return new StringBuilder(newFilenameWithoutSuffix).append(FileConstants.CN_LEFT_PARENTHESES_STR).append(count).append(FileConstants.CN_RIGHT_PARENTHESES_STR).append(newFilenameSuffix).toString();
    }

    /**
     * 查找同一个父文件夹下面的同名文件数量
     *
     * @param entity
     * @param newFilenameWithoutSuffix
     * @return
     */
    private int getDuplicateFilename(CoCloudUserFile entity, String newFilenameWithoutSuffix) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("folder_flag", entity.getFolderFlag());
        queryWrapper.eq("user_id", entity.getUserId());
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        queryWrapper.likeLeft("filename", newFilenameWithoutSuffix);
        return count(queryWrapper);
    }
}




