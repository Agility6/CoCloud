package com.coCloud.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.service.CoCloudUserService;
import com.coCloud.server.modules.user.mapper.CoCloudUserMapper;
import org.springframework.stereotype.Service;

/**
* @author agility6
* @description 针对表【co_cloud_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-05-10 19:20:36
*/
@Service
public class CoCloudUserServiceImpl extends ServiceImpl<CoCloudUserMapper, CoCloudUser>
    implements CoCloudUserService{

}




