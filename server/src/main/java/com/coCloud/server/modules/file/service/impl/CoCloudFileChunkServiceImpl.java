package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.server.modules.file.entity.CoCloudFileChunk;
import com.coCloud.server.modules.file.service.CoCloudFileChunkService;
import com.coCloud.server.modules.file.mapper.CoCloudFileChunkMapper;
import org.springframework.stereotype.Service;

/**
* @author agility6
* @description 针对表【co_cloud_file_chunk(文件分片信息表)】的数据库操作Service实现
* @createDate 2024-05-10 19:22:09
*/
@Service
public class CoCloudFileChunkServiceImpl extends ServiceImpl<CoCloudFileChunkMapper, CoCloudFileChunk>
    implements CoCloudFileChunkService{

}




