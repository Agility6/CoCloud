package com.coCloud.server.modules.user.mapper;

import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author agility6
 * @description 针对表【co_cloud_user(用户信息表)】的数据库操作Mapper
 * @createDate 2024-05-10 19:20:36
 * @Entity com.coCloud.server.modules.user.entity.CoCloudUser
 */
public interface CoCloudUserMapper extends BaseMapper<CoCloudUser> {

    /**
     * 通过用户名称查询用户设置的密保问题
     *
     * @param username
     * @return
     */
    String selectQuestionByUsername(@Param("username") String username);
}




