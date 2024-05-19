package com.coCloud.server.modules.file.converter;

import com.coCloud.server.modules.file.context.CreateFolderContext;
import com.coCloud.server.modules.file.context.DeleteFileContext;
import com.coCloud.server.modules.file.context.SecUploadFileContext;
import com.coCloud.server.modules.file.context.UpdateFilenameContext;
import com.coCloud.server.modules.file.po.CreateFolderPO;
import com.coCloud.server.modules.file.po.DeleteFilePO;
import com.coCloud.server.modules.file.po.SecUploadFilePO;
import com.coCloud.server.modules.file.po.UpdateFilenamePO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.jmx.export.annotation.ManagedOperation;

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
}
