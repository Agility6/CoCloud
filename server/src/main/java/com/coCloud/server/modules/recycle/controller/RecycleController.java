package com.coCloud.server.modules.recycle.controller;

import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.response.R;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.recycle.context.QueryRecycleFileListContext;
import com.coCloud.server.modules.recycle.context.RestoreContext;
import com.coCloud.server.modules.recycle.po.RestorePO;
import com.coCloud.server.modules.recycle.service.IRecycleService;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: RecycleController
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/22 17:58
 * @Version: 1.0
 */
@RestController
@Api(tags = "回收站模块")
@Validated
public class RecycleController {

    @Autowired
    private IRecycleService iRecycleService;

    @ApiOperation(
            value = "获取回收站文件列表",
            notes = "该接口提供了获取回收站文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("recycles")
    public R<List<CoCloudUserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(UserIdUtil.get());
        List<CoCloudUserFileVO> result = iRecycleService.recycles(context);
        return R.data(result);
    }

    @ApiOperation(
            value = "删除的文件批量还原",
            notes = "该接口提供了删除的文件批量还原的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PutMapping("recycle/restore")
    public R restore(@Validated @RequestBody RestorePO restorePO) {
        RestoreContext context = new RestoreContext();
        context.setUserId(UserIdUtil.get());

        String fileIds = restorePO.getFileIds();
        List<Long> fileIdList = Splitter.on(CoCloudConstants.COMMON_SEPARATOR).splitToList(fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);

        iRecycleService.restore(context);
        return R.success();
    }
}
