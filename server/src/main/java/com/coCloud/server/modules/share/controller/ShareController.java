package com.coCloud.server.modules.share.controller;

import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.response.R;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.modules.share.context.CreateShareUrlContext;
import com.coCloud.server.modules.share.converter.ShareConverter;
import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.coCloud.server.modules.share.po.CreateShareUrlPO;
import com.coCloud.server.modules.share.service.IShareService;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlVO;
import com.google.common.base.Splitter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
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
    public R<CoCloudShareUrlVO> create(@Validated @RequestBody CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConverter.createShareUrlPO2CreateShareUrlContext(createShareUrlPO);

        String shareFileIds = createShareUrlPO.getShareFileIds();
        List<Long> shareFileIdList = Splitter.on(CoCloudConstants.COMMON_SEPARATOR).splitToList(shareFileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());

        context.setShareFileIdList(shareFileIdList);

        CoCloudShareUrlVO vo = iShareService.create(context);
        return R.data(vo);
    }
}
