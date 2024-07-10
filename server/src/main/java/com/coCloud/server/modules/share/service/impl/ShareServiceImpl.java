package com.coCloud.server.modules.share.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coCloud.core.constants.CoCloudConstants;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.core.response.ResponseCode;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.core.utils.JwtUtil;
import com.coCloud.core.utils.UUIDUtil;
import com.coCloud.server.common.config.CoCloudServerConfig;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.share.constants.ShareConstants;
import com.coCloud.server.modules.share.context.*;
import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.coCloud.server.modules.share.enums.ShareDayTypeEnum;
import com.coCloud.server.modules.share.enums.ShareStatusEnum;
import com.coCloud.server.modules.share.service.IShareFileService;
import com.coCloud.server.modules.share.service.IShareService;
import com.coCloud.server.modules.share.mapper.CoCloudShareMapper;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlListVO;
import com.coCloud.server.modules.share.vo.CoCloudShareUrlVO;
import com.coCloud.server.modules.user.service.IUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author agility6
 * @description 针对表【co_cloud_share(用户分享表)】的数据库操作Service实现
 * @createDate 2024-05-10 19:23:23
 */
@Service
public class ShareServiceImpl extends ServiceImpl<CoCloudShareMapper, CoCloudShare> implements IShareService {

    @Autowired
    private CoCloudServerConfig config;

    @Autowired
    private IShareFileService iShareFileService;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private IUserService iUserService;

    /**
     * 创建分享链接
     * <p>
     * 1. 拼装分享实体，保存到数据库
     * 2. 保存分享和对应文件的关联关系
     * 3. 拼装返回实体并返回
     *
     * @param context
     * @return
     */
    @Transactional(rollbackFor = CoCloudBusinessException.class)
    @Override
    public CoCloudShareUrlVO create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        return assembleShareVO(context);
    }

    /**
     * 查询用户的分享列表
     *
     * @param context
     * @return
     */
    @Override
    public List<CoCloudShareUrlListVO> getShares(QueryShareListContext context) {
        return baseMapper.selectShareVOListByUserId(context.getUserId());
    }

    /**
     * 取消分享链接
     * <p>
     * 1. 校验用户操作权限
     * 2. 删除对应的分享记录
     * 3. 删除对应的分享文件关联关系记录
     *
     * @param context
     */
    @Transactional(rollbackFor = CoCloudBusinessException.class)
    @Override
    public void cancelShare(CancelShareContext context) {
        checkUserCancelSharePermission(context);
        doCancelShare(context);
        doCancelShareFiles(context);
    }

    /**
     * 校验分享码
     * <p>
     * 1. 检查分享的状态是不是正常
     * 2. 校验分享的分享码是不是正确
     * 3. 生成一个短时间的分享token 返回给上游
     *
     * @param context
     * @return
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        CoCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        doCheckShareCode(context);
        return generateShareToken(context);
    }

    /* =============> private <============= */

    /**
     * 拼装分享的实体，并保存到数据库中
     *
     * @param context
     */
    private void saveShare(CreateShareUrlContext context) {

        // 创建CoCloudShare的entity对象
        CoCloudShare record = new CoCloudShare();

        // set属性
        record.setShareId(IdUtil.get());
        record.setShareName(context.getShareName());
        record.setShareType(context.getShareType());
        record.setShareDayType(context.getShareDayType());

        // 根据context中的shareDayType获取分享天数
        Integer shareDay = ShareDayTypeEnum.getShareDayByCode(context.getShareDayType());
        if (Objects.equals(CoCloudConstants.MINUS_ONE_INT, shareDay)) {
            throw new CoCloudBusinessException("分享天数非法");
        }


        // set分享天数
        record.setShareDay(shareDay);
        // set分享的截止事件
        record.setShareEndTime(DateUtil.offsetDay(new Date(), shareDay));
        // set分享的链接
        record.setShareUrl(createShareUrl(record.getShareId()));
        // set分享的code
        record.setShareCode(createShareCode());
        // set分享的状态
        record.setShareStatus(ShareStatusEnum.NORMAL.getCode());
        // set创建分享的人
        record.setCreateUser(context.getUserId());
        // set创建时间
        record.setCreateTime(new Date());

        // 保存数据
        if (!save(record)) {
            throw new CoCloudBusinessException("保存分享信息失败");
        }

        context.setRecord(record);
    }

    /**
     * 创建分享的分享码
     *
     * @return
     */
    private String createShareCode() {
        // 返回RandomStringUtils.randomAlphabetic
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    /**
     * 创建分享的URL
     *
     * @param shareId
     * @return
     */
    private String createShareUrl(Long shareId) {
        // 判空
        if (Objects.isNull(shareId)) {
            throw new CoCloudBusinessException("分享的ID不能为空");
        }

        // 获取分享的前缀
        String sharePrefix = config.getSharePrefix();

        // 如果最后不是"/"分隔符拼接"/"
        if (sharePrefix.lastIndexOf(CoCloudConstants.SLASH_STR) == CoCloudConstants.MINUS_ONE_INT) {
            sharePrefix += CoCloudConstants.SLASH_STR;
        }

        return sharePrefix + shareId;
    }

    /**
     * 保存分享和分享文件的关联关系
     *
     * @param context
     */
    private void saveShareFiles(CreateShareUrlContext context) {
        // 创建SaveShareFileContext
        SaveShareFilesContext saveShareFilesContext = new SaveShareFilesContext();
        // set属性
        saveShareFilesContext.setShareId(context.getRecord().getShareId());
        saveShareFilesContext.setShareFileIdList(context.getShareFileIdList());
        saveShareFilesContext.setUserId(context.getUserId());
        // 调用iShareFileService的saveShareFiles方法
        iShareFileService.saveShareFiles(saveShareFilesContext);
    }

    /**
     * 拼装对应的返回VO
     *
     * @param context
     * @return
     */
    private CoCloudShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        CoCloudShare record = context.getRecord();
        CoCloudShareUrlVO vo = new CoCloudShareUrlVO();
        vo.setShareId(record.getShareId());
        vo.setShareStatus(record.getShareStatus());
        vo.setShareCode(record.getShareCode());
        vo.setShareUrl(record.getShareUrl());
        vo.setShareName(record.getShareName());
        return vo;
    }

    /**
     * 校验分享码是否正确
     *
     * @param context
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        CoCloudShare record = context.getRecord();
        if (!Objects.equals(context.getShareCode(), record.getShareCode())) {
            throw new CoCloudBusinessException("分享码错误");
        }

    }


    /**
     * 生成一个短期的分享token
     *
     * @param context
     * @return
     */
    private String generateShareToken(CheckShareCodeContext context) {
        CoCloudShare record = context.getRecord();
        return JwtUtil.generateToken(UUIDUtil.getUUID(), ShareConstants.SHARE_ID, record.getShareId(), ShareConstants.ONE_HOUR_LONG);
    }

    /**
     * 检查分享的状态是不是正常
     *
     * @param shareId
     * @return
     */
    private CoCloudShare checkShareStatus(Long shareId) {

        // 通过shareId获取entity
        CoCloudShare record = getById(shareId);

        // 判空
        if (Objects.isNull(record)) {
            throw new CoCloudBusinessException(ResponseCode.SHARE_EXPIRE);
        }

        // entity状态是FILE_DELETED抛异常
        if (Objects.equals(ShareStatusEnum.FILE_DELETED.getCode(), record.getShareStatus())) {
            throw new CoCloudBusinessException(ResponseCode.SHARE_FILE_MISS);
        }

        // 截止时间是永久直接返回
        if (Objects.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareDayType())) {
            return record;
        }

        // 判断是否大于截止时间
        if (record.getShareEndTime().before(new Date())) {
            throw new CoCloudBusinessException(ResponseCode.SHARE_EXPIRE);
        }

        return record;
    }

    /**
     * 取消文件和分享的关联关系数据
     *
     * @param context
     */
    private void doCancelShareFiles(CancelShareContext context) {
        // QueryWrapper查询
        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.eq("create_user", context.getUserId());
        queryWrapper.in("share_id", context.getShareIdList());
        if (!iShareFileService.remove(queryWrapper)) {
            throw new CoCloudBusinessException("取消分享失败");
        }

    }

    /**
     * 执行取消文件分享的动作
     *
     * @param context
     */
    private void doCancelShare(CancelShareContext context) {
        // 获取shareIdList
        List<Long> shareIdList = context.getShareIdList();
        // 删除数据库
        if (!removeByIds(shareIdList)) {
            throw new CoCloudBusinessException("取消分享失败");
        }
    }

    /**
     * 检查用户是否拥有取消对应分享链接的权限
     *
     * @param context
     */
    private void checkUserCancelSharePermission(CancelShareContext context) {
        // 从context中获取shareIdList
        List<Long> shareIdList = context.getShareIdList();
        // 从context中获取userId
        Long userId = context.getUserId();

        // 从db获取shareIdList
        List<CoCloudShare> records = listByIds(shareIdList);
        // 判空
        if (CollectionUtil.isEmpty(records)) {
            throw new CoCloudBusinessException("无权操作取消分享动作");
        }

        // 遍历records是否全部都为userId
        for (CoCloudShare record : records) {
            if (!Objects.equals(userId, record.getCreateUser())) {
                throw new CoCloudBusinessException("您无权限操作取消分享的动作");
            }
        }
    }

}
