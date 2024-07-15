package com.coCloud.server.modules.file.converter;

import com.coCloud.server.modules.file.context.*;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.po.*;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.file.vo.FolderTreeNodeVO;
import com.coCloud.storage.engine.core.context.StoreFileChunkContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.boot.actuate.autoconfigure.web.server.ConditionalOnManagementPort;
import org.springframework.jmx.export.annotation.ManagedOperation;

import java.util.Map;

/**
 * ClassName: FileConverter
 * Description: 文件模块实体转化工具类
 *
 * @Author agility6
 * @Create 2024/5/18 15:27
 * @Version: 1.0
 */
@Mapper(componentModel = "spring")
public interface FileConverter {

    // 因为parentId是加密的，因此在获取的时候先进行解密
    @Mapping(target = "parentId", expression = "java(com.coCloud.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    // 获取userId
    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    CreateFolderContext createFolderPO2CreateFolderContext(CreateFolderPO createFolderPO);

    @Mapping(target = "fileId", expression = "java(com.coCloud.core.utils.IdUtil.decrypt(updateFilenamePO.getFileId()))")
    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    UpdateFilenameContext updateFilenamePO2UpdateFilenameContext(UpdateFilenamePO updateFilenamePO);

    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    DeleteFileContext deleteFilePO2DeleteFileContext(DeleteFilePO deleteFilePO);

    @Mapping(target = "parentId", expression = "java(com.coCloud.core.utils.IdUtil.decrypt(secUploadFilePO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    SecUploadFileContext secUploadFilePO2SecUploadFileContext(SecUploadFilePO secUploadFilePO);

    @Mapping(target = "parentId", expression = "java(com.coCloud.core.utils.IdUtil.decrypt(fileUploadPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    FileUploadContext fileUploadPO2FileUploadContext(FileUploadPO fileUploadPO);


    // 忽略CoCloudFile属性
    @Mapping(target = "record", ignore = true)
    FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext fileUploadContext);

    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    FileChunkUploadContext fileChunkUploadPO2FileChunkUploadContext(FileChunkUploadPO fileChunkUploadPO);

    FileChunkSaveContext fileChunkUploadContext2FileChunkSaveContext(FileChunkUploadContext context);

    // 忽略realPath的映射
    @Mapping(target = "realPath", ignore = true)
    StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context);

    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    QueryUploadedChunksContext queryUploadedChunksPO2QueryUploadedChunksContext(QueryUploadedChunksPO queryUploadedChunksPO);

    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    @Mapping(target = "parentId", expression = "java(com.coCloud.core.utils.IdUtil.decrypt(fileChunkMergePO.getParentId()))")
    FileChunkMergeContext fileChunkMergePO2FileChunkMergeContext(FileChunkMergePO fileChunkMergePO);


    FileChunkMergeAndSaveContext fileChunkMergeContext2FileChunkMergeAndSaveContext(FileChunkMergeContext context);

    @Mapping(target = "label", source = "record.filename")
    @Mapping(target = "id", source = "record.fileId")
    @Mapping(target = "children", expression = "java(com.google.common.collect.Lists.newArrayList())")
    FolderTreeNodeVO coCloudUserFile2FolderTreeNodeVO(CoCloudUserFile record);

    CoCloudUserFileVO coCloudUserFile2CoCloudUserFileVO(CoCloudUserFile record);
}
