package com.coCloud.server.modules.share.controller;

import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.response.R;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.annotation.LoginIgnore;
import com.coCloud.server.common.annotation.NeedShareCode;
import com.coCloud.server.common.utils.ShareIdUtil;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.share.context.*;
import com.coCloud.server.modules.share.converter.ShareConverter;
import com.coCloud.server.modules.share.po.CancelSharePO;
import com.coCloud.server.modules.share.po.CheckShareCodePO;
import com.coCloud.server.modules.share.po.CreateShareUrlPO;
import com.coCloud.server.modules.share.service.IShareService;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlListVO;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlVO;
import com.coCloud.server.modules.share.vo.ShareDetailVO;
import com.coCloud.server.modules.share.vo.ShareSimpleDetailVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ClassName: ShareController
 * Description:
 *
 * @Author agility6
 * @Create 2024/7/9 14:14
 * @Version: 1.0
 */
@Api(tags = "分享模块")
@RestController
@Validated
public class ShareController {

    @Autowired
    private IShareService iShareService;

    @Autowired
    private ShareConverter shareConverter;

    @ApiOperation(
            value = "创建分享链接",
            notes = "该接口提供了创建分享链接的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @PostMapping("share")
    public R<CoCloudShareUrlVO> create(@Validated @RequestBody CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConverter.createShareUrlPO2CreateShareUrlContext(createShareUrlPO);

        String shareFileIds = createShareUrlPO.getShareFileIds();
        List<Long> shareFileIdList = Splitter.on(CoCloudConstants.COMMON_SEPARATOR).splitToList(shareFileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());

        context.setShareFileIdList(shareFileIdList);

        CoCloudShareUrlVO vo = iShareService.create(context);
        return R.data(vo);
    }

    @ApiOperation(
            value = "查询分享链接列表",
            notes = "该接口提供了查询分享链接列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("shares")
    public R<List<CoCloudShareUrlListVO>> getShares() {
        QueryShareListContext context = new QueryShareListContext();
        context.setUserId(UserIdUtil.get());
        List<CoCloudShareUrlListVO> result = iShareService.getShares(context);
        return R.data(result);
    }

    @ApiOperation(
            value = "取消分享",
            notes = "该接口提供了取消分享的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("share")
    public R cancelShare(@Validated @RequestBody CancelSharePO cancelSharePO) {
        CancelShareContext context = new CancelShareContext();
        context.setUserId(UserIdUtil.get());
        List<Long> shareIdList = Splitter.on(CoCloudConstants.COMMON_SEPARATOR).splitToList(cancelSharePO.getShareIds()).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setShareIdList(shareIdList);

        iShareService.cancelShare(context);
        return R.success();
    }

    @ApiOperation(
            value = "校验分享码",
            notes = "该接口提供了校验分享码的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore // 不需要登录
    @PostMapping("share/code/check")
    public R<String> checkShareCode(@Validated @RequestBody CheckShareCodePO checkShareCodePO) {
        CheckShareCodeContext context = new CheckShareCodeContext();

        context.setShareId(IdUtil.decrypt(checkShareCodePO.getShareId()));
        context.setShareCode(checkShareCodePO.getShareCode());

        String token = iShareService.checkShareCode(context);
        return R.data(token);
    }

    @ApiOperation(
            value = "查询分享的详情",
            notes = "该接口提供了查询分享的详情的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @NeedShareCode
    @GetMapping("share")
    public R<ShareDetailVO> detail() {
        QueryShareDetailContext context = new QueryShareDetailContext();
        context.setShareId(ShareIdUtil.get());
        ShareDetailVO vo = iShareService.detail(context);
        return R.data(vo);
    }

    @ApiOperation(
            value = "查询分享的简单详情",
            notes = "该接口提供了查询分享的简单详情的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @GetMapping("share/simple")
    public R<ShareSimpleDetailVO> simpleDetail(@NotBlank(message = "分享的ID不能为空") @RequestParam(value = "shareId", required = false) String shareId) {
        QueryShareSimpleDetailContext context = new QueryShareSimpleDetailContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleDetailVO vo = iShareService.simpleDetail(context);
        return R.data(vo);
    }

    @ApiOperation(
            value = "获取下一级文件列表",
            notes = "该接口提供了获取下一级文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @NeedShareCode
    @LoginIgnore
    @GetMapping("share/file/list")
    public R<List<CoCloudUserFileVO>> fileList(@NotBlank(message = "文件的父ID不能为空") @RequestParam(value = "parentId", required = false) String parentId) {
        QueryChildFileListContext context = new QueryChildFileListContext();
        context.setShareId(ShareIdUtil.get());
        context.setParentId(IdUtil.decrypt(parentId));
        List<CoCloudUserFileVO> result = iShareService.fileList(context);
        return R.data(result);
    }

}
