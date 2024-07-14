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
import com.coCloud.server.modules.file.context.QueryFileListContext;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.share.constants.ShareConstants;
import com.coCloud.server.modules.share.context.*;
import com.coCloud.server.modules.share.entity.CoCloudShare;
import com.coCloud.server.modules.share.enums.ShareDayTypeEnum;
import com.coCloud.server.modules.share.enums.ShareStatusEnum;
import com.coCloud.server.modules.share.service.IShareFileService;
import com.coCloud.server.modules.share.service.IShareService;
import com.coCloud.server.modules.share.mapper.CoCloudShareMapper;
import com.coCloud.server.modules.share.vo.*;
import com.coCloud.server.modules.user.entity.CoCloudUser;
import com.coCloud.server.modules.user.service.IUserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
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

    /**
     * 查询分享的详情
     * <p>
     * 1. 校验分享的状态
     * 2. 初始化分享实体
     * 3. 查询分享的主体信息
     * 4. 查询分享的文件列表
     * 5. 查询分享者的信息
     *
     * @param context
     * @return
     */
    @Override
    public ShareDetailVO detail(QueryShareDetailContext context) {
        // 校验分享状态
        CoCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareVO(context);
        assembleMainShareInfo(context);
        assembleShareFilesInfo(context);
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     * 查询分享的简单详情
     * <p>
     * 1. 校验分享的状态
     * 2. 初始化分享实体
     * 3. 查询分享的主体信息
     * 4. 查询分享者的信息
     *
     * @param context
     * @return
     */
    @Override
    public ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context) {
        CoCloudShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareSimpleVO(context);
        assembleMainShareSimpleInfo(context);
        assembleShareSimpleUserInfo(context);
        return context.getVo();
    }

    /**
     * 瓶装简单文件分享详情的用户信息
     *
     * @param context
     */
    private void assembleShareSimpleUserInfo(QueryShareSimpleDetailContext context) {
        // 获取CreateUser的entity
        CoCloudUser record = iUserService.getById(context.getRecord().getCreateUser());

        // 判空
        if (Objects.isNull(record)) {
            throw new CoCloudBusinessException("用户信息查询失败");
        }

        // 创建shareUserInfoVO
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        // set属性
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 填充简单分享详情实体信息
     *
     * @param context
     */
    private void assembleMainShareSimpleInfo(QueryShareSimpleDetailContext context) {
        // 从context中获取record
        CoCloudShare record = context.getRecord();
        // 从context中获取vo
        ShareSimpleDetailVO vo = context.getVo();

        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
    }

    /**
     * 初始化简单分享详情的VO对象
     *
     * @param context
     */
    private void initShareSimpleVO(QueryShareSimpleDetailContext context) {
        ShareSimpleDetailVO vo = new ShareSimpleDetailVO();
        context.setVo(vo);
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

    /**
     * 查询分享者的信息
     *
     * @param context
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        // 通过CreateUserId获取User的entity对象
        CoCloudUser record = iUserService.getById(context.getRecord().getCreateUser());
        // 判空
        if (Objects.isNull(record)) {
            throw new CoCloudBusinessException("用户信息查询失败");
        }

        // 创建ShareUserInfoVO对象
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        // 装配属性
        shareUserInfoVO.setUsername(encryptUsername(record.getUsername()));
        shareUserInfoVO.setUserId(record.getUserId());

        // 放入到context中
        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    private String encryptUsername(String username) {
        StringBuffer stringBuffer = new StringBuffer(username);
        stringBuffer.replace(CoCloudConstants.TWO_INT, username.length() - CoCloudConstants.TWO_INT, CoCloudConstants.COMMON_ENCRYPT_STR);
        return stringBuffer.toString();
    }

    /**
     * 查询分享对应的文件列表
     * <p>
     * 1. 查询分享对应的文件ID
     * 2. 根据文件ID来查询文件列表信息
     *
     * @param context
     */
    private void assembleShareFilesInfo(QueryShareDetailContext context) {
        // 获取shareFileIdList
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        // 创建QueryFileListContext
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        // 装配属性
        queryFileListContext.setUserId(context.getRecord().getCreateUser());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileIdList(fileIdList);

        // 调用UserFileService的getFileList
        List<CoCloudUserFileVO> coCloudUserFileVOList = iUserFileService.getFileList(queryFileListContext);
        // 装配到context中
        context.getVo().setCoCloudUserFileVOList(coCloudUserFileVOList);
    }

    /**
     * 查询分享对应的文件ID集合
     *
     * @param shareId
     * @return
     */
    private List<Long> getShareFileIdList(Long shareId) {
        if (Objects.isNull(shareId)) {
            return Lists.newArrayList();
        }

        QueryWrapper queryWrapper = Wrappers.query();
        queryWrapper.select("file_id");
        queryWrapper.eq("share_id", shareId);
        List<Long> fileIdList = iShareFileService.listObjs(queryWrapper, value -> (Long) value);
        return fileIdList;
    }

    /**
     * 查询分享的主体信息
     *
     * @param context
     */
    private void assembleMainShareInfo(QueryShareDetailContext context) {
        // 获取context中的CoCloudShare
        CoCloudShare record = context.getRecord();
        // 获取context中的vo对象
        ShareDetailVO vo = context.getVo();
        // 设置属性
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setCreateTime(record.getCreateTime());
        vo.setShareDay(record.getShareDay());
        vo.setShareEndTime(record.getShareEndTime());
    }

    /**
     * 初始化文件详情的VO实体
     *
     * @param context
     */
    private void initShareVO(QueryShareDetailContext context) {
        // 创建ShareDetailVO对象
        ShareDetailVO vo = new ShareDetailVO();
        // 添加到context中
        context.setVo(vo);

    }


}
