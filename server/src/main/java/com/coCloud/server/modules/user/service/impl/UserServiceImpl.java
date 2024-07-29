package com.coCloud.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.cache.core.constants.CacheConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.response.ResponseCode;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.core.utils.JwtUtil;
import com.coCloud.core.utils.PasswordUtil;
import com.coCloud.server.common.cache.AnnotationCacheService;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.CreateFolderContext;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.user.constants.UserConstants;
import com.coCloud.server.modules.user.context.*;
import com.coCloud.server.modules.user.converter.UserConverter;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.service.IUserService;
import com.coCloud.server.modules.user.mapper.CoCloudUserMapper;
import com.coCloud.server.modules.user.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author agility6
 * @description 针对表【co_cloud_user(用户信息表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:20:36
 */
@Service(value = "userService")
public class UserServiceImpl extends ServiceImpl<CoCloudUserMapper, CoCloudUser> implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    @Qualifier(value = "userAnnotationCacheService")
    private AnnotationCacheService<CoCloudUser> cacheService;

    /**
     * 用户注册业务实现
     * 1. 注册用户信息
     * 2. 创建新用户的根目录信息
     * <p>
     * 该业务是幂等的，要保证用户名全局唯一
     * 幂等性通过数据库表对于用户名字段添加唯一索引，我们上有业务捕获对应的冲突异常，转化返回
     *
     * @param userRegisterContext
     * @return
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        // 装配UserEntity到Context中
        assembleUserEntity(userRegisterContext);
        // 注册
        doRegister(userRegisterContext);
        // 创建用户根目录
        createUserRootFolder(userRegisterContext);
        // 返回用户Id
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * <p>
     * 需要实现的功能
     * 1. 用户的登录信息校验
     * 2. 生成一个具有时效性的accessToken
     * 3. 将accessToken缓存起来，实现单机登录
     *
     * @param userLoginContext
     * @return
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        // 校验信息
        checkLoginInfo(userLoginContext);
        // 生成并且保存token
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }

    /**
     * 用户退出登录
     * <p>
     * 1. 清除用户的登录凭证缓存
     *
     * @param userId
     */
    @Override
    public void exit(Long userId) {
        try {
            // 获取缓存数据
            Cache cache = cacheManager.getCache(CacheConstants.CO_CLOUD_CACHE_NAME);
            // 清除缓存
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CoCloudBusinessException("用户退出登录失败");
        }
    }

    /**
     * 忘记用户密码-校验用户名称
     *
     * @param checkUsernameContext
     * @return
     */
    @Override
    public String checkUsername(CheckUsernameContext checkUsernameContext) {
        // 获取该账户的密保问题
        String question = baseMapper.selectQuestionByUsername(checkUsernameContext.getUsername());
        // 如果不存在说明没该用户
        if (StringUtils.isBlank(question)) {
            throw new CoCloudBusinessException("没有此用户");
        }
        return question;
    }

    /**
     * 用户忘记密码—校验密保答案
     *
     * @param checkAnswerContext
     * @return
     */
    @Override
    public String checkAnswer(CheckAnswerContext checkAnswerContext) {
        // 查询数据库验证
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", checkAnswerContext.getUsername());
        queryWrapper.eq("question", checkAnswerContext.getQuestion());
        queryWrapper.eq("answer", checkAnswerContext.getAnswer());

        int count = count(queryWrapper);

        if (count == 0) {
            throw new CoCloudBusinessException("密保答案错误");
        }

        // 生成一个临时的token给前端以便后续修改密码校验
        return generateCheckAnswerToken(checkAnswerContext);

    }

    /**
     * 重置密码
     * 1. 校验token是不是有效
     * 2. 重置密码
     *
     * @param resetPasswordContext
     */
    @Override
    public void resetPassword(ResetPasswordContext resetPasswordContext) {
        // 校验忘记token
        checkForgetPasswordToken(resetPasswordContext);
        // 重置密码
        checkAndResetUserPassword(resetPasswordContext);
    }

    /**
     * 在线修改密码
     * <p>
     * 1. 校验旧密码
     * 2. 重置新密码
     * 3. 退出当前登录状态
     *
     * @param changePasswordContext
     */
    @Override
    public void changePassword(ChangePasswordContext changePasswordContext) {
        // 校验旧密码
        checkOldPassword(changePasswordContext);
        // 重置新密码
        doChangePassword(changePasswordContext);
        // 退出当前登录
        exitLoginStatus(changePasswordContext);
    }

    /**
     * 查询在线用户的基本信息
     * <p>
     * 1. 查询用户的基本信息实体
     * 2. 查询用户的根文件信息
     * 3. 拼装VO对象返回
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO info(Long userId) {
        // 通过Id获取User的entity
        CoCloudUser entity = getById(userId);
        // 判空
        if (Objects.isNull(entity)) {
            throw new CoCloudBusinessException("用户信息查询失败");
        }

        // 通过Id获取FileInfo
        CoCloudUserFile coCloudUserFile = getUserRootFileInfo(userId);
        // 判空
        if (Objects.isNull(coCloudUserFile)) {
            throw new CoCloudBusinessException("查询用户根目录夹信息失败");
        }


        // 将User的entity和UserFile转化为VO
        return userConverter.assembleUserInfoVO(entity, coCloudUserFile);
    }

    // 重写IService的方法，引入缓存

    /**
     * 根据ID删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    /**
     * 删除（根据ID 批量删除）
     *
     * @param idList
     * @return
     */
    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        throw new CoCloudBusinessException("请更换手动缓存");
    }

    /**
     * 根据ID 选择修改
     *
     * @param entity
     * @return
     */
    @Override
    public boolean updateById(CoCloudUser entity) {
        return cacheService.updateById(entity.getUserId(), entity);
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList
     * @return
     */
    @Override
    public boolean updateBatchById(Collection<CoCloudUser> entityList) {
        throw new CoCloudBusinessException("请更换手动缓存");
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public CoCloudUser getById(Serializable id) {
        return cacheService.getById(id);
    }

    /**
     * 查询（根据ID 批量查询）
     *
     * @param idList
     * @return
     */
    @Override
    public List<CoCloudUser> listByIds(Collection<? extends Serializable> idList) {
        throw new CoCloudBusinessException("请更换手动缓存");
    }


    /* =============> private <============= */

    /**
     * 生成并保存登录之后的凭证
     *
     * @param userLoginContext
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        CoCloudUser entity = userLoginContext.getEntity();

        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID, entity.getUserId(), UserConstants.ONE_DAY_LONG);

        Cache cache = cacheManager.getCache(CacheConstants.CO_CLOUD_CACHE_NAME);
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);

        userLoginContext.setAccessToken(accessToken);

    }


    /**
     * 校验用户名密码
     *
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {

        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        CoCloudUser entity = getCoCloudByUsername(username);

        // 校验用户名
        if (Objects.isNull(entity)) {
            throw new CoCloudBusinessException("用户名称不存在！");
        }

        // 校验密码
        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(encPassword, dbPassword)) {
            throw new CoCloudBusinessException("密码信息不正确！");
        }

        // 将Entity添加到Context中
        userLoginContext.setEntity(entity);
    }

    /**
     * 通过用户名获取用户实体信息
     *
     * @param username
     * @return
     */
    private CoCloudUser getCoCloudByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }


    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        iUserFileService.createFolder(createFolderContext);
    }


    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突异常，来实现全局用户名称唯一性
     *
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        // 获取entity
        CoCloudUser entity = userRegisterContext.getEntity();
        if (Objects.nonNull(entity)) {
            try {
                // 数据库保存失败
                if (!save(entity)) {
                    throw new CoCloudBusinessException("用户注册失败");
                }
            } catch (DuplicateKeyException duplicateKeyException) { // 唯一索引冲突
                throw new CoCloudBusinessException("用户名已存在");
            }
            return;
        }
        // Entity异常
        throw new CoCloudBusinessException(ResponseCode.ERROR);
    }


    /**
     * 实体转化
     * 将上下文信息转化为用户实体，封装到上下文中
     *
     * @param userRegisterContext
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        // 将Context转化为UserEntity
        CoCloudUser entity = userConverter.userRegisterContext2CoCloudUser(userRegisterContext);
        // 获取盐值加密密码
        String salt = PasswordUtil.getSalt(), dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        // 雪花算法生产Id
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(dbPassword);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        // 将UserEntity封装到Context中
        userRegisterContext.setEntity(entity);
    }

    /**
     * 生成用户名忘记密码-校验密保答案通过的临时token
     * token的失效时间为五分钟
     *
     * @param checkAnswerContext
     * @return
     */
    private String generateCheckAnswerToken(CheckAnswerContext checkAnswerContext) {
        return JwtUtil.generateToken(checkAnswerContext.getUsername(), UserConstants.FORGET_USERNAME, checkAnswerContext.getUsername(), UserConstants.FIVE_MINUTES_LONG);
    }

    /**
     * 验证用户信息并重置用户密码
     *
     * @param resetPasswordContext
     */
    private void checkAndResetUserPassword(ResetPasswordContext resetPasswordContext) {
        String username = resetPasswordContext.getUsername();
        String password = resetPasswordContext.getPassword();

        CoCloudUser entity = getCoCloudByUsername(username);

        if (Objects.isNull(entity)) {
            throw new CoCloudBusinessException("用户信息不存在");
        }

        // 加密新的密码
        String newDbPassword = PasswordUtil.encryptPassword(entity.getSalt(), password);

        entity.setPassword(newDbPassword);
        entity.setUpdateTime(new Date());

        if (!updateById(entity)) {
            throw new CoCloudBusinessException("重置用户密码失败");
        }

    }

    /**
     * 验证忘记密码的token是否是有效的
     *
     * @param resetPasswordContext
     */
    private void checkForgetPasswordToken(ResetPasswordContext resetPasswordContext) {
        String token = resetPasswordContext.getToken();
        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USERNAME);
        // token过期
        if (Objects.isNull(value)) {
            throw new CoCloudBusinessException(ResponseCode.TOKEN_EXPIRE);
        }
        String tokenUsername = String.valueOf(value);
        // 解析token是否正确
        if (Objects.equals(tokenUsername, resetPasswordContext.getUsername())) {
            throw new CoCloudBusinessException("token错误");
        }

    }

    /**
     * 退出用户的登录状态
     *
     * @param changePasswordContext
     */
    private void exitLoginStatus(ChangePasswordContext changePasswordContext) {
        exit(changePasswordContext.getUserId());
    }

    /**
     * 修改新密码
     *
     * @param changePasswordContext
     */
    private void doChangePassword(ChangePasswordContext changePasswordContext) {
        // 获取新密码
        String newPassword = changePasswordContext.getNewPassword();
        // 获取上下文的entity
        CoCloudUser entity = changePasswordContext.getEntity();
        // 获取salt
        String salt = entity.getSalt();

        // 加密新密码
        String encNewPassword = PasswordUtil.encryptPassword(salt, newPassword);

        // 保证到entity中
        entity.setPassword(encNewPassword);

        // 更新数据库
        if (!updateById(entity)) {
            throw new CoCloudBusinessException("修改用户密码失败");
        }
    }

    /**
     * 校验用户旧密码
     * 该步骤会将查询并且封装到实体上下文对象中
     *
     * @param changePasswordContext
     */
    private void checkOldPassword(ChangePasswordContext changePasswordContext) {
        // 获取用户Id
        Long userId = changePasswordContext.getUserId();
        // 获取旧密码
        String oldPassword = changePasswordContext.getOldPassword();

        // 通过id获取entity
        CoCloudUser entity = getById(userId);
        // 判空
        if (Objects.isNull(entity)) {
            throw new CoCloudBusinessException("用户信息不存在");
        }
        // 放入Context中
        changePasswordContext.setEntity(entity);

        // 加密旧密码
        String encOldPassword = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        // 获取db密码
        String dbOldPassword = entity.getPassword();
        // 判断是否一致
        if (!Objects.equals(encOldPassword, dbOldPassword)) {
            throw new CoCloudBusinessException("旧密码不正确");
        }

    }

    /**
     * 获取用户根文件夹信息实体
     *
     * @param userId
     * @return
     */
    private CoCloudUserFile getUserRootFileInfo(Long userId) {
        return iUserFileService.getUserRootFile(userId);
    }


}




