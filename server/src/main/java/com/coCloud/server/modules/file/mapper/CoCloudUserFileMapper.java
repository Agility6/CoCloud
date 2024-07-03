package com.coCloud.server.modules.file.mapper;

import com.coCloud.server.modules.file.context.FileSearchContext;
import com.coCloud.server.modules.file.context.QueryCountFoldersContext;
import com.coCloud.server.modules.file.context.QueryFileListContext;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.file.vo.FileSearchResultVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author agility6
 * @description 针对表【co_cloud_user_file(用户文件信息表)】的数据库操作Mapper
 * @createDate 2024-05-10 19:22:09
 * @Entity com.coCloud.server.modules.file.entity.CoCloudUserFile
 */
public interface CoCloudUserFileMapper extends BaseMapper<CoCloudUserFile> {

    /**
     * 查询用户的文件列表
     *
     * @param context
     * @return
     */
    List<CoCloudUserFileVO> selectFileList(@Param("param") QueryFileListContext context);

    /**
     * 文件搜素
     *
     * @param context
     * @return
     */
    List<FileSearchResultVO> searchFile(@Param("param") FileSearchContext context);

    int countFoldersByName(@Param("param") QueryCountFoldersContext context);
}




