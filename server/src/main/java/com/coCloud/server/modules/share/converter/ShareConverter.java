package com.coCloud.server.modules.share.converter;

import com.coCloud.server.modules.share.context.CreateShareUrlContext;
import com.coCloud.server.modules.share.po.CreateShareUrlPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ClassName: ShareConverter
 * Description: 分享模块实体转换工具类
 *
 * @Author agility6
 * @Create 2024/7/9 14:16
 * @Version: 1.0
 */
@Mapper(componentModel = "spring")
public interface ShareConverter {

    @Mapping(target = "userId", expression = "java(com.coCloud.server.common.utils.UserIdUtil.get())")
    CreateShareUrlContext createShareUrlPO2CreateShareUrlContext(CreateShareUrlPO createShareUrlPO);

}
