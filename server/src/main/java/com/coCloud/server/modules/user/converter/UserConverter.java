package com.coCloud.server.modules.user.converter;

import com.coCloud.server.modules.user.context.UserLoginContext;
import com.coCloud.server.modules.user.context.UserRegisterContext;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.po.UserLoginPO;
import com.coCloud.server.modules.user.po.UserRegisterPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * ClassName: UserConverter
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/13 11:11
 * @Version: 1.0
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * UserRegisterPO转化成UserRegisterContext
     *
     * @param userRegisterPO
     * @return
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    /**
     * UserRegisterContext转CoCloudUser
     * 忽略密码
     *
     * @param userRegisterContext
     * @return
     */
    @Mapping(target = "password", ignore = true)
    CoCloudUser userRegisterContext2CoCloudUser(UserRegisterContext userRegisterContext);

    /**
     * userLoginPO转UserLoginContext
     *
     * @param userLoginPO
     * @return
     */
    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);
}
