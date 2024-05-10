package com.coCloud.server.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.server.modules.log.entity.CoCloudErrorLog;
import com.coCloud.server.modules.log.service.CoCloudErrorLogService;
import com.coCloud.server.modules.log.mapper.CoCloudErrorLogMapper;
import org.springframework.stereotype.Service;

/**
* @author agility6
* @description 针对表【co_cloud_error_log(错误日志表)】的数据库操作Service实现
* @createDate 2024-05-10 19:22:29
*/
@Service
public class CoCloudErrorLogServiceImpl extends ServiceImpl<CoCloudErrorLogMapper, CoCloudErrorLog>
    implements CoCloudErrorLogService{

}




