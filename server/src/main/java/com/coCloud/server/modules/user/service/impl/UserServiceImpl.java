package com.coCloud.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.cache.core.constants.CacheConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.response.ResponseCode;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.core.utils.JwtUtil;
import com.coCloud.core.utils.PasswordUtil;
import com.coCloud.server.modules.file.constants.FileConstants;
import com.coCloud.server.modules.file.context.CreateFolderContext;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.user.constants.UserConstants;
import com.coCloud.server.modules.user.context.UserLoginContext;
import com.coCloud.server.modules.user.context.UserRegisterContext;
import com.coCloud.server.modules.user.converter.UserConverter;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.service.IUserService;
import com.coCloud.server.modules.user.mapper.CoCloudUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
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
     * 3. 将accessToken缓存取来，实现单机登录
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
}




