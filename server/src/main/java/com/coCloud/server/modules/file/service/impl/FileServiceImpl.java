package com.coCloud.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.server.modules.file.context.QueryRealFileListContext;
import com.coCloud.server.modules.file.entity.CoCloudFile;
import com.coCloud.server.modules.file.service.IFileService;
import com.coCloud.server.modules.file.mapper.CoCloudFileMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author agility6
 * @description 针对表【co_cloud_file(物理文件信息表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:22:09
 */
@Service
public class FileServiceImpl extends ServiceImpl<CoCloudFileMapper, CoCloudFile> implements IFileService {

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<CoCloudFile> getFileList(QueryRealFileListContext context) {
        // 获取userID
        Long userId = context.getUserId();
        // 获取identifier
        String identifier = context.getIdentifier();
        // 查询数据库
        LambdaQueryWrapper<CoCloudFile> queryWrapper = new LambdaQueryWrapper<>();
        // 如果userId不为null
        queryWrapper.eq(Objects.nonNull(userId), CoCloudFile::getCreateUser, userId);
        // 如果 identifier 不为 null 且不为空白字符串，
        queryWrapper.eq(StringUtils.isNotBlank(identifier), CoCloudFile::getIdentifier, identifier);
        return list(queryWrapper);

    }
}




