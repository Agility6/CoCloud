package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.service.CoCloudFileService;
import com.coCloud.server.modules.file.mapper.CoCloudFileMapper;
import org.springframework.stereotype.Service;

/**
* @author agility6
* @description 针对表【co_cloud_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2024-05-10 19:22:09
*/
@Service
public class CoCloudFileServiceImpl extends ServiceImpl<CoCloudFileMapper, CoCloudFile>
    implements CoCloudFileService{

}




