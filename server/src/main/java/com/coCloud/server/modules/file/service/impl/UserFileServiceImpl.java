package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.utils.FileUtils;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.event.file.DeleteFileEvent;
import com.coCloud.server.common.event.search.UserSearchEvent;
import com.coCloud.server.common.utils.HttpUtil;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.*;
import com.coCloud.server.modules.file.converter.FileConverter;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.enums.FileTypeEnum;
import com.coCloud.server.modules.file.enums.FolderFlagEnum;
import com.coCloud.server.modules.file.service.IFileChunkService;
import com.coCloud.server.modules.file.service.IFileService;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.mapper.CoCloudUserFileMapper;
import com.coCloud.server.modules.file.vo.*;
import com.coCloud.storage.engine.core.StorageEngine;
import com.coCloud.storage.engine.core.context.ReadFileContext;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;
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
    private IFileChunkService iFileChunkService;

    @Autowired
    private FileConverter fileConverter;

    @Autowired
    private StorageEngine storageEngine;

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
            saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())), record.getFileId(), context.getUserId(), record.getFileSizeDesc());
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
        saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())), context.getRecord().getFileId(), context.getUserId(), context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件分片上传
     * <p>
     * 1. 上传实体文件
     * 2. 保存分片文件记录
     * 3. 校验是否全部分片上传完成
     *
     * @param context
     * @return
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        // 将context转化为FileChunkSaveContext交给FileChunkService实现
        FileChunkSaveContext fileChunkSaveContext = fileConverter.fileChunkUploadContext2FileChunkSaveContext(context);
        // 保存分片文件记录
        iFileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO vo = new FileChunkUploadVO();
        vo.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return vo;
    }

    /**
     * 查询用户已上传的分片列表
     * <p>
     * 1. 查询已上传的分片列表
     * 2. 封装返回实体
     *
     * @param context
     * @return
     */
    @Override
    public UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context) {
        // 查询数据库
        QueryWrapper queryWrapper = Wrappers.query();
        // 选择字段，chunk_number
        queryWrapper.select("chunk_number");
        // identifier
        queryWrapper.eq("identifier", context.getIdentifier());
        // create_user
        queryWrapper.eq("create_user", context.getUserId());
        // expiration_time 大于当前时间
        queryWrapper.gt("expiration_time", new Date());

        // listObjs查询并返回指定条件下的数据列表，并且转化为Integer，（注意这里交给FileChunkService处理）
        List<Integer> uploadedChunks = iFileChunkService.listObjs(queryWrapper, value -> (Integer) value);
        // 封装vo返回
        UploadedChunksVO vo = new UploadedChunksVO();
        vo.setUploadedChunks(uploadedChunks);
        return vo;

    }

    /**
     * 文件分片合并
     * <p>
     * 1. 文件分片物理合并
     * 2. 保存文件实体记录
     * 3. 保存文件用户关系映射
     *
     * @param context
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        // 文件分片物理合并
        mergeFileChunkAndSaveFile(context);
        // 保存文件用户关系映射
        saveUserFile(context.getParentId(), context.getFilename(), FolderFlagEnum.NO, FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())), context.getRecord().getFileId(), context.getUserId(), context.getRecord().getFileSizeDesc());
    }

    /**
     * 文件下载
     * <p>
     * 1. 参数校验：校验文件是否存在，文件是否属于该用户
     * 2. 校验该文件是不是一个文件夹
     * 3. 执行下载动作
     *
     * @param context
     */
    @Override
    public void download(FileDownloadContext context) {
        // 通过fileId获取UserFile
        CoCloudUserFile record = getById(context.getFileId());
        // 验证文件是否属于该用户
        checkOperatePermission(record, context.getUserId());
        // 校验文件是否是一个文件夹
        if (checkIsFolder(record)) {
            throw new CoCloudBusinessException("文件暂不支持下载");
        }
        // 执行下载
        doDownload(record, context.getResponse());
    }

    /**
     * 文件预览
     * <p>
     * 1. 参数校验：校验文件是否存在，文件是否属于该用户
     * 2. 校验该文件是不是一个文件夹
     * 3. 执行预览动作
     *
     * @param context
     */
    @Override
    public void preview(FilePreviewContext context) {
        CoCloudUserFile record = getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (checkIsFolder(record)) {
            throw new CoCloudBusinessException("文件夹暂不支持预览");
        }
        doPreview(record, context.getResponse());
    }

    /**
     * 查询用户的文件夹树
     * <p>
     * 1. 查询出该用户的所有文件夹列表
     * 2. 在内存中拼装文件树
     *
     * @param context
     * @return
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context) {
        List<CoCloudUserFile> folderRecords = queryFolderRecords(context.getUserId());
        List<FolderTreeNodeVO> result = assembleFolderTreeNodeVOList(folderRecords);
        return result;
    }

    /**
     * 文件转移
     * <p>
     * 1. 权限校验
     * 2. 执行工作
     *
     * @param context
     */
    @Override
    public void transfer(TransferFileContext context) {
        checkTransferCondition(context);
        doTransfer(context);
    }

    /**
     * 文件复制
     * <p>
     * 1. 条件校验
     * 2. 执行动作
     *
     * @param context
     */
    @Override
    public void copy(CopyFileContext context) {
        checkCopyCondition(context);
        doCopy(context);
    }

    /**
     * 文件列表搜素
     * <p>
     * 1. 执行文件搜素
     * 2. 拼装文件的父文件名称
     * 3. 执行文件搜素后的后置动作
     *
     * @param context
     * @return
     */
    @Override
    public List<FileSearchResultVO> search(FileSearchContext context) {
        List<FileSearchResultVO> result = doSearch(context);
        // 可能存在不同父文件中有相同文件名称
        fillParentFilename(result);
        afterSearch(context);
        return null;
    }

    /**
     * 获取面包屑列表
     * <p>
     * 1. 获取用户所有文件夹信息
     * 2. 拼接需要用到的面包屑的列表
     *
     * @param context
     * @return
     */
    @Override
    public List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context) {
        // 获取当前用户的所有文件夹
        List<CoCloudUserFile> folderRecords = queryFolderRecords(context.getUserId());
        // 将文件夹记录转换为 BreadcrumbVO 对象并存储到 Map 中
        Map<Long, BreadcrumbVO> prepareBreadcrumbVOMap = folderRecords.stream().map(BreadcrumbVO::transfer).collect(Collectors.toMap(BreadcrumbVO::getId, a -> a));

        // 初始化面包屑列表
        BreadcrumbVO currentNode;
        Long fileId = context.getFileId();
        LinkedList<BreadcrumbVO> result = Lists.newLinkedList();

        // 遍历父文件夹链条，生成面包屑列表
        do {
            currentNode = prepareBreadcrumbVOMap.get(fileId);
            if (Objects.nonNull(currentNode)) {
                // 将当前节点添加到结果的开头
                result.add(0, currentNode);
                // 更新 fileId 为当前节点的父节点ID
                fileId = currentNode.getParentId();
            }
        } while (Objects.nonNull(currentNode));
        return result;
    }

    /**
     * 递归查询所有的子文件信息
     *
     * @param records
     * @return
     */
    @Override
    public List<CoCloudUserFile> findAllFileRecords(List<CoCloudUserFile> records) {
        // 创建result数组，将records作为初始化
        List<CoCloudUserFile> result = Lists.newArrayList(records);
        // 如果为空直接返回
        if (CollectionUtils.isEmpty(result)) {
            return result;
        }
        // 查询result数组中有多少个是文件夹
        long folderCount = result.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).count();
        // 如果没有直接返回
        if (folderCount == 0) {
            return result;
        }
        // 遍历records执行doFindAllChildRecords将result和records传入
        // TODO filter???
        records.stream().forEach(record -> doFindAllChildRecords(result, record));

        return result;
    }

    /**
     * 递归查询所有的子文件信息
     *
     * @param fileIdList
     * @return
     */
    @Override
    public List<CoCloudUserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList) {
        // fileIdList是否为空
        if (CollectionUtils.isEmpty(fileIdList)) {
            return Lists.newArrayList();
        }

        // 查询fileIdList
        List<CoCloudUserFile> records = listByIds(fileIdList);

        // 判空
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }

        // 递归
        return findAllFileRecords(records);
    }

    /**
     * 实体转换
     *
     * @param records
     * @return
     */
    @Override
    public List<CoCloudUserFileVO> transferVOList(List<CoCloudUserFile> records) {
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return records.stream().map(fileConverter::coCloudUserFile2CoCloudUserFileVO).collect(Collectors.toList());
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
        QueryCountFoldersContext context = new QueryCountFoldersContext();
        context.setParentId(entity.getParentId());
        context.setFolderFlag(entity.getFolderFlag());
        context.setUserId(entity.getUserId());
        context.setDelFlag(DelFlagEnum.NO.getCode());
        context.setFilename(newFilenameWithoutSuffix);
        return baseMapper.countFoldersByName(context);
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


    /**
     * 合并文件分片并保存物理文件记录
     *
     * @param context
     */
    private void mergeFileChunkAndSaveFile(FileChunkMergeContext context) {
        // 将context转化为FileChunkMergeAndSaveContext
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConverter.fileChunkMergeContext2FileChunkMergeAndSaveContext(context);
        // 交给FileService处理
        iFileService.mergeFileChunkAndSaveFile(fileChunkMergeAndSaveContext);
        // 将UserFile添加到context中
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }

    /**
     * 执行文件下载的动作
     * <p>
     * 1. 查询文件的真实存储路径
     * 2. 添加跨域的公共响应头
     * 3. 拼装下载文件的名称，长度等等响应信息
     * 4. 委托文件存储引擎去读取文件内容响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doDownload(CoCloudUserFile record, HttpServletResponse response) {
        // 通过RealFileId获取realFileRecord
        CoCloudFile realFileRecord = iFileService.getById(record.getRealFileId());
        // 添加公共的文件读取响应头
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 添加文件下载的属性信息
        addDownloadAttribute(response, record, realFileRecord);
        // 委托文件存储引擎去读取文件内容并写入到输出流中
        realFile2OutputStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 委托文件存储引擎去读取文件内容并写入到输出流中
     *
     * @param realPath
     * @param response
     */
    private void realFile2OutputStream(String realPath, HttpServletResponse response) {
        try {
            // 创建readFileContext
            ReadFileContext context = new ReadFileContext();
            // set属性
            context.setRealPath(realPath);
            context.setOutputStream(response.getOutputStream());
            // 委托引擎处理
            storageEngine.realFile(context);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CoCloudBusinessException("文件下载失败");
        }
    }

    /**
     * 添加文件下载的属性信息
     *
     * @param response
     * @param record
     * @param realFileRecord
     */
    private void addDownloadAttribute(HttpServletResponse response, CoCloudUserFile record, CoCloudFile realFileRecord) {
        try {
            response.addHeader(FileConstants.CONTENT_DISPOSITION_STR, FileConstants.CONTENT_DISPOSITION_VALUE_PREFIX_STR + new String(record.getFilename().getBytes(FileConstants.GB2312_STR), FileConstants.IOS_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            // 编码不支持
            throw new CoCloudBusinessException("文件下载失败");
        }
        // 设置下载的长度
        response.setContentLengthLong(Long.valueOf(realFileRecord.getFileSize()));
    }

    /**
     * 添加公共的文件读取响应头
     *
     * @param response
     * @param contentTypeValue
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentTypeValue) {
        // 清除之前的任何数据和头信息
        response.reset();
        // 添加跨域相关的响应头
        HttpUtil.addCorsResponseHeaders(response);
        // 添加报文主体的对象类型
        response.addHeader(FileConstants.CONTENT_TYPE_STR, contentTypeValue);
        // 设置响应对象的内容类型
        response.setContentType(contentTypeValue);
    }

    /**
     * 检查当前文件记录是不是一个文件夹
     *
     * @param record
     * @return
     */
    private boolean checkIsFolder(CoCloudUserFile record) {
        if (Objects.isNull(record)) {
            throw new CoCloudBusinessException("当前文件记录不存在");
        }
        return FolderFlagEnum.YES.getCode().equals(record.getFolderFlag());
    }

    /**
     * 校验用户的操作权限
     * <p>
     * 1. 文件记录必须存在
     * 2. 文件记录的创建者必须是该登录用户
     *
     * @param record
     * @param userId
     */
    private void checkOperatePermission(CoCloudUserFile record, Long userId) {
        if (Objects.isNull(record)) {
            throw new CoCloudBusinessException("当前文件记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new CoCloudBusinessException("您当前没有操作权限");
        }
    }

    /**
     * 执行文件预览的动作
     * <p>
     * 1. 查询文件的真实存储路径
     * 2. 添加跨域的公共响应头
     * 3. 委托文件存储引擎去读取文件内容到响应的输出流中
     *
     * @param record
     * @param response
     */
    private void doPreview(CoCloudUserFile record, HttpServletResponse response) {
        // 获取realFileRecord
        CoCloudFile realFileRecord = iFileService.getById(record.getRealFileId());
        if (Objects.isNull(realFileRecord)) {
            throw new CoCloudBusinessException("当前的文件记录不存在");
        }
        // 添加跨域公共响应头
        addCommonResponseHeader(response, realFileRecord.getFilePreviewContentType());
        realFile2OutputStream(realFileRecord.getRealPath(), response);

    }

    /**
     * 拼装文件夹树列表
     *
     * @param folderRecords
     * @return
     */
    private List<FolderTreeNodeVO> assembleFolderTreeNodeVOList(List<CoCloudUserFile> folderRecords) {
        if (CollectionUtils.isEmpty(folderRecords)) {
            return Lists.newArrayList();
        }
        List<FolderTreeNodeVO> mappedFolderTreeNodeVOList = folderRecords.stream().map(fileConverter::coCloudUserFile2FolderTreeNodeVO).collect(Collectors.toList());
        // 通过parentId分组 ==> parentId等于上一层File的ID
        Map<Long, List<FolderTreeNodeVO>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream().collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));
        for (FolderTreeNodeVO node : mappedFolderTreeNodeVOList) {
            // 当前File的子节点等于Map中Key为当前文件Id的集合
            List<FolderTreeNodeVO> children = mappedFolderTreeNodeVOMap.get(node.getId());
            if (CollectionUtils.isNotEmpty(children)) {
                node.getChildren().addAll(children);
            }
        }
        // 顶层节点过滤返回
        return mappedFolderTreeNodeVOList.stream().filter(node -> Objects.equals(node.getParentId(), FileConstants.TOP_PARENT_ID)).collect(Collectors.toList());
    }

    /**
     * 查询用户所有有效的文件夹信息
     *
     * @param userId
     * @return
     */
    private List<CoCloudUserFile> queryFolderRecords(Long userId) {
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        return list(queryWrapper);
    }

    /***
     * 执行文件复制动作
     *
     * @param context
     */
    private void doCopy(CopyFileContext context) {
        // 获取要转移文件列表的实体对象
        List<CoCloudUserFile> prepareRecords = context.getPrepareRecords();
        if (CollectionUtils.isNotEmpty(prepareRecords)) {
            List<CoCloudUserFile> allRecords = Lists.newArrayList();
            prepareRecords.stream()
                    // 装配
                    .forEach(record -> assembleCopyChildRecord(allRecords, record, context.getTargetParentId(), context.getUserId()));
            if (!saveBatch(allRecords)) {
                throw new CoCloudBusinessException("文件复制失败");
            }
        }
    }

    /**
     * 拼装当前文件记录以及所有的子文件记录
     *
     * @param allRecords
     * @param record
     * @param targetParentId
     * @param userId
     */
    private void assembleCopyChildRecord(List<CoCloudUserFile> allRecords, CoCloudUserFile record, Long targetParentId, Long userId) {
        // 获取新文件的ID
        Long newFileId = IdUtil.get();
        // 获取旧文件的ID
        Long oldFileId = record.getFileId();

        // Set属性到record中
        record.setParentId(targetParentId);
        record.setFileId(newFileId);
        record.setUserId(userId);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        record.setUpdateUser(userId);
        record.setUpdateTime(new Date());
        // 处理重命名
        handleDuplicateFilename(record);

        // 将record存入allRecords中
        allRecords.add(record);

        // 如果当前record是文件，需要递归处理
        if (checkIsFolder(record)) {
            // 查找下一级的文件记录
            List<CoCloudUserFile> childRecords = findChildRecords(oldFileId);
            // 如果不为空那么递归处理
            if (CollectionUtils.isEmpty(childRecords)) {
                return;
            }
            childRecords.stream().forEach(childRecord -> assembleCopyChildRecord(allRecords, childRecord, newFileId, userId));
        }
    }

    /**
     * 查找下一级的文件记录
     *
     * @param parentId
     * @return
     */
    private List<CoCloudUserFile> findChildRecords(Long parentId) {
        QueryWrapper queryWrapper = Wrappers.query();
        // 文件的parentId
        queryWrapper.eq("parent_id", parentId);
        // 不是删除状态
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        return list(queryWrapper);
    }

    /**
     * 文件转移的条件校验
     * <p>
     * 1. 目标文件夹必须是一个文件夹
     * 2. 选中要转移的文件列表找中不能含有目标文件夹以及其其子文件夹
     *
     * @param context
     */
    private void checkCopyCondition(CopyFileContext context) {
        // 获取目标文件夹的ID
        Long targetParentId = context.getTargetParentId();
        // 校验是否是文件交
        if (!checkIsFolder(getById(targetParentId))) {
            throw new CoCloudBusinessException("目标文件不是一个文件夹");
        }

        // 获取要转移的文件夹列表ID
        List<Long> fileIdList = context.getFileIdList();
        // 通过列表ID获取要转移文件列表的实体对象prepareRecords
        List<CoCloudUserFile> prepareRecords = listByIds(fileIdList);
        // 将prepareRecords放入到context中
        context.setPrepareRecords(prepareRecords);
        // checkIsChildFolder
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new CoCloudBusinessException("目标文件夹ID不能是选中文件列表的文件ID或其子文件夹ID");
        }
    }

    /**
     * 执行文件转移的动作
     *
     * @param context
     */
    private void doTransfer(TransferFileContext context) {
        // 获取要转移的文件列表
        List<CoCloudUserFile> prepareRecords = context.getPrepareRecords();
        // 逐一修改属性
        prepareRecords.stream().forEach(record -> {
            // 修改文件的ParentId
            record.setParentId(context.getTargetParentId());
            record.setUserId(context.getUserId());
            record.setCreateUser(context.getUserId());
            record.setCreateTime(new Date());
            record.setUpdateUser(context.getUserId());
            record.setUpdateTime(new Date());
            // 处理文件夹重名
            handleDuplicateFilename(record);
        });
        if (!updateBatchById(prepareRecords)) {
            throw new CoCloudBusinessException("文件转移失败");
        }

    }

    /**
     * 文件转移的条件校验
     * <p>
     * 1. 目标文件必须是一个文件夹
     * 2. 选中的要转移的文件列表中不能含有目标文件及其子文件夹
     *
     * @param context
     */
    private void checkTransferCondition(TransferFileContext context) {
        // 获取目标位置的parentId
        Long targetParentId = context.getTargetParentId();
        // 判断是不是文件夹
        if (!checkIsFolder(getById(targetParentId))) {
            throw new CoCloudBusinessException("目标文件不是一个文件夹");
        }
        // 获取要转移的文件ID集合
        List<Long> fileIdList = context.getFileIdList();
        // 通过文件ID集合获取要转移的文件列表
        List<CoCloudUserFile> prepareRecords = listByIds(fileIdList);
        // 将要转移的文件列表实体放入context中
        context.setPrepareRecords(prepareRecords);

        // 检查要转移的文件夹是否合法
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new CoCloudBusinessException("目标文件夹ID不能是选中文件列表的文件夹ID或其子文件夹ID");
        }
    }

    /**
     * 校验目标文件夹ID是否是要操作的文件记录的文件夹ID以及其子文件夹ID
     * <p>
     * 1. 如果要操作的文件列表中没有文件夹，那么就直接返回false
     * 2. 拼装文件夹ID以及所有子文件夹ID，判断存在即可
     *
     * @param prepareRecords
     * @param targetParentId
     * @param userId
     * @return
     */
    private boolean checkIsChildFolder(List<CoCloudUserFile> prepareRecords, Long targetParentId, Long userId) {
        // 将prepareRecords过滤只保留文件夹
        prepareRecords = prepareRecords.stream().filter(record -> Objects.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).collect(Collectors.toList());
        // 判断是否为空，如果为空直接返回false
        if (CollectionUtils.isEmpty(prepareRecords)) {
            return false;
        }

        // 通过用户ID查询用户所有有效的文件夹信息
        List<CoCloudUserFile> coCloudUserFiles = queryFolderRecords(userId);
        // 通过parentId作为key构建Map集合
        Map<Long, List<CoCloudUserFile>> folderRecordMap = coCloudUserFiles.stream().collect(Collectors.groupingBy(CoCloudUserFile::getParentId));

        // 创建一个列表，用于存储所有不可用的文件夹记录
        List<CoCloudUserFile> unavailableFolderRecords = Lists.newArrayList();
        unavailableFolderRecords.addAll(prepareRecords);

        // 递归查找并收集所有子文件夹记录
        prepareRecords.stream().forEach(record -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, record));

        // 从不可用的文件夹记录中提取文件夹 ID 列表
        List<Long> unavailableFolderRecordIds = unavailableFolderRecords.stream().map(CoCloudUserFile::getFileId).collect(Collectors.toList());
        return unavailableFolderRecordIds.contains(targetParentId);
    }

    /**
     * 查询文件夹的所有子文件夹记录
     *
     * @param unavailableFolderRecords
     * @param folderRecordMap
     * @param record
     */
    private void findAllChildFolderRecords(List<CoCloudUserFile> unavailableFolderRecords, Map<Long, List<CoCloudUserFile>> folderRecordMap, CoCloudUserFile record) {
        if (Objects.isNull(record)) {
            return;
        }
        // 获取当前文件夹的子文件夹
        List<CoCloudUserFile> childFolderRecords = folderRecordMap.get(record.getFileId());
        if (CollectionUtils.isEmpty(childFolderRecords)) {
            return;
        }
        unavailableFolderRecords.addAll(childFolderRecords);
        // 递归调用
        childFolderRecords.stream().forEach(childRecord -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, childRecord));

    }


    /**
     * 递归查询所有的子文件列表，忽略是否删除的标识
     *
     * @param result
     * @param record
     */
    private void doFindAllChildRecords(List<CoCloudUserFile> result, CoCloudUserFile record) {
        // 如果record为空直接返回
        if (Objects.isNull(record)) {
            return;
        }
        // 如果record不为文件夹类型直接返回
        if (!checkIsFolder(record)) {
            return;
        }

        // 执行findChildRecordsIgnoreDelFlag，传入record的fileId，获取所有的子文件
        List<CoCloudUserFile> childRecords = findChildRecordsIgnoreDelFlag(record.getFileId());
        // 如果childRecords为空直接返回
        if (CollectionUtils.isEmpty(childRecords)) {
            return;
        }

        // 将childRecords添加到result中
        result.addAll(childRecords);
        // 递归查询childRecords的数据，过滤符合文件夹的
        childRecords.stream()
                .filter(childRecord -> FolderFlagEnum.YES.getCode().equals(childRecord.getFolderFlag()))
                .forEach(childRecord -> doFindAllChildRecords(result, childRecord));
    }

    /**
     * 查询文件夹下面的文件记录，忽略删除标识
     *
     * @param fileId
     * @return
     */
    private List<CoCloudUserFile> findChildRecordsIgnoreDelFlag(Long fileId) {
        // QueryWrapper查询，parentId是fileId即可
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("parent_id", fileId);
        List<CoCloudUserFile> childRecords = list(queryWrapper);
        return childRecords;
    }

    /**
     * 搜索的后置操作
     * <p>
     * 1. 发布文件搜索事件
     *
     * @param context
     */
    private void afterSearch(FileSearchContext context) {
        UserSearchEvent event = new UserSearchEvent(this, context.getKeyword(), context.getUserId());
        applicationContext.publishEvent(event);
    }

    /**
     * 填充文件列表的父文件名称
     *
     * @param result
     */
    private void fillParentFilename(List<FileSearchResultVO> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }
        List<Long> parentIdList = result.stream().map(FileSearchResultVO::getParentId).collect(Collectors.toList());
        List<CoCloudUserFile> parentRecords = listByIds(parentIdList);
        Map<Long, String> fileId2filenameMap = parentRecords.stream().collect(Collectors.toMap(CoCloudUserFile::getFileId, CoCloudUserFile::getFilename));
        result.stream().forEach(vo -> vo.setParentFilename(fileId2filenameMap.get(vo.getParentId())));
    }

    /**
     * 搜素文件列表
     *
     * @param context
     * @return
     */
    private List<FileSearchResultVO> doSearch(FileSearchContext context) {
        return baseMapper.searchFile(context);
    }
}
