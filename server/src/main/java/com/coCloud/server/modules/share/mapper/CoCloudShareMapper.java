package com.coCloud.server.modules.share.mapper;

import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author agility6
 * @description 针对表【co_cloud_share(用户分享表)】的数据库操作Mapper
 * @createDate 2024-05-10 19:23:23
 * @Entity com.coCloud.server.modules.share.entity.CoCloudShare
 */
public interface CoCloudShareMapper extends BaseMapper<CoCloudShare> {

    /**
     * 查询用户的分享列表
     *
     * @param userId
     * @return
     */
    List<CoCloudShareUrlListVO> selectShareVOListByUserId(@Param("userId") Long userId);
}




