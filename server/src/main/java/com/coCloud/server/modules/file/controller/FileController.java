package com.coCloud.server.modules.file.controller;

import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.response.R;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.*;
import com.coCloud.server.modules.file.converter.FileConverter;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.po.CreateFolderPO;
import com.coCloud.server.modules.file.po.DeleteFilePO;
import com.coCloud.server.modules.file.po.SecUploadFilePO;
import com.coCloud.server.modules.file.po.UpdateFilenamePO;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassName: FileController
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/16 20:29
 * @Version: 1.0
 */
@RestController
@Validated
@Api(tags = "文件模块")
public class FileController {

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private FileConverter fileConverter;

    public R<List<CoCloudUserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false) String parentId,
                                           @RequestParam(value = "fileType", required = false, defaultValue = FileConstants.ALL_FILE_TYPE) String fileTypes) {
        // 解密Id
        Long realParentId = IdUtil.decrypt(parentId);
        List<Integer> fileTypeArray = null;

        if (!Objects.equals(FileConstants.ALL_FILE_TYPE, fileTypes)) {
            fileTypeArray = Splitter.on(CoCloudConstants.COMMON_SEPARATOR).splitToList(fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
        }

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(realParentId);
        context.setFileTypeArray(fileTypeArray);
        context.setUserId(UserIdUtil.get());
        context.setDelFlag(DelFlagEnum.NO.getCode());

        List<CoCloudUserFileVO> result = iUserFileService.getFileList(context);
        return R.data(result);
    }

    @ApiOperation(
            value = "创建文件夹",
            notes = "该接口提供了创建文件夹的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("file/folder")
    public R<String> createFolder(@Validated @RequestBody CreateFolderPO createFolderPO) {
        CreateFolderContext context = fileConverter.createFolderPO2CreateFolderContext(createFolderPO);
        Long fileId = iUserFileService.createFolder(context);
        return R.data(IdUtil.encrypt(fileId));
    }

    @ApiOperation(
            value = "文件重命名",
            notes = "该接口提供了文件重命名的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PutMapping("file")
    public R updateFilename(@Validated @RequestBody UpdateFilenamePO updateFilenamePO) {
        UpdateFilenameContext context = fileConverter.updateFilenamePO2UpdateFilenameContext(updateFilenamePO);
        iUserFileService.updateFilename(context);
        return R.success();
    }

    @ApiOperation(
            value = "批量删除文件",
            notes = "该接口提供了批量删除文件的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("file")
    public R deleteFile(@Validated @RequestBody DeleteFilePO deleteFilePO) {
        DeleteFileContext context = fileConverter.deleteFilePO2DeleteFileContext(deleteFilePO);

        String fileIds = deleteFilePO.getFileIds();

        // 将所有的fileId进行解密使用List存放
        List<Long> fileIdList = Splitter.on(CoCloudConstants.COMMON_SEPARATOR).splitToList(fileIds).stream()
                .map(IdUtil::decrypt)
                .collect(Collectors.toList());

        context.setFileIdList(fileIdList);
        iUserFileService.deleteFile(context);
        return R.success();
    }

    @ApiOperation(
            value = "文件秒传",
            notes = "该接口提供了文件秒传的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("file/sec-upload")
    public R secUpload(@Validated @RequestBody SecUploadFilePO secUploadFilePO) {
        SecUploadFileContext context = fileConverter.secUploadFilePO2SecUploadFileContext(secUploadFilePO);
        boolean result = iUserFileService.secUpload(context);
        if (result) {
            return R.success();
        }
        return R.fail("文件唯一标识不存在，请手动执行文件上传");
    }


}
