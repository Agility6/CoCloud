package com.coCloud.server.modules.user.converter;

import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.user.context.*;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.po.*;
import com.coCloud.server.modules.user.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

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

    /**
     * checkUsernamePO转CheckUsernameContext
     *
     * @param checkUsernamePO
     * @return
     */
    CheckUsernameContext checkUsernamePO2CheckUsernameContext(CheckUsernamePO checkUsernamePO);

    /**
     * CheckAnswerPO转CheckAnswerContext
     *
     * @param checkAnswerPO
     * @return
     */
    CheckAnswerContext checkAnswerPO2CheckAnswerContext(CheckAnswerPO checkAnswerPO);

    /**
     * ResetPasswordPO转ResetPasswordContext
     *
     * @param resetPasswordPO
     * @return
     */
    ResetPasswordContext resetPasswordPO2ResetPasswordContext(ResetPasswordPO resetPasswordPO);

    /**
     * ChangePasswordPO转ChangePasswordContext
     *
     * @param changePasswordPO
     * @return
     */
    ChangePasswordContext changePasswordPO2ChangePasswordContext(ChangePasswordPO changePasswordPO);

    /**
     * 拼装用户基本信息返回实体
     * <p>
     * 1. 绑定username
     * 2. 绑定fileId --> rootFileId
     * 3. 绑定fileID --> rootFilename
     *
     * @param coCloudUser
     * @param coCloudUserFile
     * @return
     */
    @Mapping(source = "coCloudUser.username", target = "username")
    @Mapping(source = "coCloudUserFile.fileId", target = "rootFileId")
    @Mapping(source = "coCloudUserFile.filename", target = "rootFilename")
    UserInfoVO assembleUserInfoVO(CoCloudUser coCloudUser, CoCloudUserFile coCloudUserFile);
}
