package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.utils.FileUtils;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.event.file.DeleteFileEvent;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.*;
import com.coCloud.server.modules.file.converter.FileConverter;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.enums.FileTypeEnum;
import com.coCloud.server.modules.file.enums.FolderFlagEnum;
import com.coCloud.server.modules.file.service.IFileService;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.mapper.CoCloudUserFileMapper;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author agility6
 * @description 针对表【co_cloud_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:22:09
 */
@Service(value = "userFileService")
public class UserFileServiceImpl extends ServiceImpl<CoCloudUserFileMapper, CoCloudUserFile> implements IUserFileService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private FileConverter fileConverter;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    /**
     * 创建文件夹信息
     *
     * @param createFolderContext
     * @return
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getParentId(), createFolderContext.getFolderName(), FolderFlagEnum.YES, null, null, createFolderContext.getUserId(), null);
    }

    /**
     * 查询用户的根目录夹信息
     *
     * @param userId
     * @return
     */
    @Override
    public CoCloudUserFile getUserRootFile(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("parent_id", FileConstants.TOP_PARENT_ID);
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        return getOne(queryWrapper);
    }

    /**
     * 查询用户的文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<CoCloudUserFileVO> getFileList(QueryFileListContext context) {
        return baseMapper.selectFileList(context);
    }

    /**
     * 更新文件名称
     * <p>
     * 1. 校验更新文件名称的条件
     * 2. 执行更新文件名称的操作
     *
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    /**
     * 批量删除用户文件
     * <p>
     * 1. 校验删除的条件
     * 2. 执行批量删除的动作
     * 3. 发布批量删除文件的事件，给其他模块订阅使用
     *
     * @param context
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        checkFileDeleteCondition(context);
        doDeleteFile(context);
        afterFileDelete(context);
    }

    /**
     * 文件妙传功能
     * <p>
     * 1. 判断用户之前是否上传过该文件
     * 2. 如果上传过该文件，只需要生成一个该文件和当前用户在指定文件夹下面的关联
     *
     * @param context
     * @return true 代表用户之前上传过相同文件并成功挂在了关联关系
     */
    @Override
    public boolean secUpload(SecUploadFileContext context) {
        // 查询用户文件列表根据文件的唯一标识
        List<CoCloudFile> fileList = getFileListByUserIdAndIdentifier(context.getUserId(), context.getIdentifier());
        // 判断如果不为空，条件记录即可
        if (CollectionUtils.isNotEmpty(fileList)) {
            // 获取之前的file文件
            CoCloudFile record = fileList.get(CoCloudConstants.ZERO_INT);
            saveUserFile(context.getParentId(),
                    context.getFilename(),
                    FolderFlagEnum.NO,
                    FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())),
                    record.getFileId(),
                    context.getUserId(),
                    record.getFileSizeDesc());
            return true;
        }
        return false;
    }

    /**
     * 单文件上传
     * <p>
     * 1. 上传文件并保存实体文件的记录
     * 2. 保存用户文件的关系记录
     *
     * @param context
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(FileUploadContext context) {
        saveFile(context);
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc());
    }

    /**
     * 上传文件并保存实体文件记录
     * 委托给实体文件的Service去完成操作
     *
     * @param context
     */
    private void saveFile(FileUploadContext context) {
        // 将FileUploadContext转换为FileSaveContext
        FileSaveContext fileSaveContext = fileConverter.fileUploadContext2FileSaveContext(context);
        iFileService.saveFile(fileSaveContext);
        // 保存实体文件记录，在保存用户文件关系记录中使用
        context.setRecord(fileSaveContext.getRecord());
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
        return entity.getFileId();
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

    /**
     * 执行文件重命名的操作
     *
     * @param context
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        // 从context获取entity
        CoCloudUserFile entity = context.getEntity();
        // 更新新名字、更新人、更新时间
        entity.setFilename(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());
        entity.setUpdateTime(new Date());

        // update
        if (!updateById(entity)) {
            throw new CoCloudBusinessException("文件重命名失败");
        }
    }

    /**
     * 更新文件名称的条件校验
     * <p>
     * 0. 文件ID是有效的
     * 1. 用户有权限更新文件的文件名称
     * 2. 新旧文件名称不能一样
     * 3. 不能使用当前文件夹下面的子文件的名称
     *
     * @param context
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {

        // 获取fileId
        Long fileId = context.getFileId();
        // 通过fileId获取UserFile entity
        CoCloudUserFile entity = getById(fileId);

        // 判空
        if (Objects.isNull(entity)) {
            throw new CoCloudBusinessException("该文件ID无效");
        }

        // entity中的userId与context不一致，无权限
        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new CoCloudBusinessException("当前登录用户没有修改文件名称的权限");
        }

        // 新旧文件一致
        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new CoCloudBusinessException("请换一个新的文件名称修改");
        }

        // 查询名称是否已经被占用
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());

        int count = count(queryWrapper);

        // 已占用
        if (count > 0) {
            throw new CoCloudBusinessException("该文件名称已经被占用");
        }

        // entity放入context中
        context.setEntity(entity);
    }

    /**
     * 文件删除的后置操作
     * <p>
     * 1. 对外发布文件删除的事件
     *
     * @param context
     */
    private void afterFileDelete(DeleteFileContext context) {
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, context.getFileIdList());
        applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 执行文件删除的操作
     *
     * @param context
     */
    private void doDeleteFile(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        // 更新
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.in("file_id", fileIdList);
        // 标记为删除
        updateWrapper.set("del_flag", DelFlagEnum.YES.getCode());
        updateWrapper.set("update_time", new Date());

        if (!update(updateWrapper)) {
            throw new CoCloudBusinessException("文件删除失败");
        }
    }

    /**
     * 删除文件之前的置换校验
     * <p>
     * 1. 文件ID合法校验
     * 2. 用户拥有删除文件的权限
     *
     * @param context
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {

        // context中获取fileIdList
        List<Long> fileIdList = context.getFileIdList();

        // 通过fileIdList从数据库获取userFile entity
        List<CoCloudUserFile> coCloudUserFiles = listByIds(fileIdList);
        // 判断数量是否匹配
        if (coCloudUserFiles.size() != fileIdList.size()) {
            throw new CoCloudBusinessException("存在不合法的文件记录");
        }

        // 从db的fileUsers中获取所有fileId使用Set集合
        Set<Long> fileIdSet = coCloudUserFiles.stream().map(CoCloudUserFile::getFileId).collect(Collectors.toSet());
        // 再添加fileIdList判断值是否相等
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();

        if (oldSize != newSize) {
            throw new CoCloudBusinessException("存在不合法的文件记录");
        }

        // 从db的fileUsers中获取userId存入集合中
        Set<Long> userIdSet = coCloudUserFiles.stream().map(CoCloudUserFile::getUserId).collect(Collectors.toSet());
        // 如果set size不等于1则错误
        if (userIdSet.size() != 1) {
            throw new CoCloudBusinessException("存在不合法的文件记录");
        }


        // 获取userId
        Long dnUserId = userIdSet.stream().findFirst().get();
        // 判断userId与context中userId是否相等
        if (!Objects.equals(dnUserId, context.getUserId())) {
            throw new CoCloudBusinessException("当前登录用户没有删除该文件的权限");
        }
    }

    /**
     * 查询用户文件列表根据文件的唯一标识
     *
     * @param userId
     * @param identifier
     * @return
     */
    private List<CoCloudFile> getFileListByUserIdAndIdentifier(Long userId, String identifier) {
        QueryRealFileListContext context = new QueryRealFileListContext();
        context.setUserId(userId);
        context.setIdentifier(identifier);
        return iFileService.getFileList(context);

    }


}




