package com.coCloud.server.modules.file.controller;

import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.response.R;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.CreateFolderContext;
import com.coCloud.server.modules.file.context.QueryFileListContext;
import com.coCloud.server.modules.file.converter.FileConverter;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.po.CreateFolderPO;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public R<List<CoCloudUserFileVO>> list (@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false)String parentId,
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
}
