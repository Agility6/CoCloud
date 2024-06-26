package com.coCloud.server.modules.recycle.controller;

import com.coCloud.core.response.R;
import com.coCloud.server.common.utils.UserIdUtil;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.recycle.context.QueryRecycleFileListContext;
import com.coCloud.server.modules.recycle.service.IRecycleService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("recycles")
    public R<List<CoCloudUserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(UserIdUtil.get());
        List<CoCloudUserFileVO> result = iRecycleService.recycles(context);
        return R.data(result);
    }

}
