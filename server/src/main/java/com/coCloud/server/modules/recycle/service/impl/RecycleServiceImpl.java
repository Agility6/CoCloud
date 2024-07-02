package com.coCloud.server.modules.recycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coCloud.core.exception.CoCloudBusinessException;
import com.coCloud.server.common.event.file.FileRestoreEvent;
import com.coCloud.server.modules.file.context.QueryFileListContext;
import com.coCloud.server.modules.file.entity.CoCloudUserFile;
import com.coCloud.server.modules.file.enums.DelFlagEnum;
import com.coCloud.server.modules.file.service.IUserFileService;
import com.coCloud.server.modules.file.vo.CoCloudUserFileVO;
import com.coCloud.server.modules.recycle.context.QueryRecycleFileListContext;
import com.coCloud.server.modules.recycle.context.RestoreContext;
import com.coCloud.server.modules.recycle.service.IRecycleService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: RecycleServiceImpl
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/22 17:59
 * @Version: 1.0
 */
@Service
public class RecycleServiceImpl implements IRecycleService, ApplicationContextAware {

    @Autowired
    private IUserFileService iUserFileService;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 查询用户的回收站文件列表
     *
     * @param context
     * @return
     */
    @Override
    public List<CoCloudUserFileVO> recycles(QueryRecycleFileListContext context) {
        // 创建QueryFileListContext
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        // 设置参数
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        // 调用UserFileService的getFileList
        return iUserFileService.getFileList(queryFileListContext);
    }

    /**
     * 文件还原
     * <p>
     * 1. 检查操作权限
     * 2. 检查是不是可以还原
     * 3. 执行文件还原的操作
     * 4. 执行文件还原的后置操作
     *
     * @param context
     */
    @Override
    public void restore(RestoreContext context) {
        checkRestorePermission(context);
        checkRestoreFilename(context);
        doRestore(context);
        afterRestore(context);
    }

    /* =============> private <============= */

    /**
     * 文件还原的后置操作
     * <p>
     * 1. 发布文件还原事件
     *
     * @param context
     */
    private void afterRestore(RestoreContext context) {
        FileRestoreEvent event = new FileRestoreEvent(this, context.getFileIdList());
        applicationContext.publishEvent(event);

    }

    /**
     * 执行文件还原的动作
     *
     * @param context
     */
    private void doRestore(RestoreContext context) {
        // 获取要被还原的文件记录列表
        List<CoCloudUserFile> records = context.getRecords();
        // 将所有文件记录进行属性设置(删除标记，更新人，更新时间)
        records.stream().forEach(record -> {
            record.setDelFlag(DelFlagEnum.NO.getCode());
            record.setUpdateUser(context.getUserId());
            record.setUpdateTime(new Date());
        });
        // 更新数据库
        if (!iUserFileService.updateBatchById(records)) {
            throw new CoCloudBusinessException("文件还原失败");
        }

    }

    /**
     * 检查要还原的文件名称是不是被占用
     * <p>
     * 1. 要还原的文件列表中同一个文件夹下面有相同名称的文件 不允许还原
     * 2. 要还原的文件当前的父文件夹下面存在同名文件，不允许还原
     *
     * @param context
     */
    private void checkRestoreFilename(RestoreContext context) {
        // 获取context中的records
        List<CoCloudUserFile> records = context.getRecords();
        // 获取每一个文件名称存放到set中
        Set<String> filenameSet = records.stream().map(record -> record.getFilename()).collect(Collectors.toSet());
        // 判断set中的长度是否和要被还原的文件记录列表长度一致
        if (filenameSet.size() != records.size()) {
            throw new CoCloudBusinessException("文件还原失败，该还原文件中存在同名文件，请修改");
        }

        // 遍历records分别查找，防止还原文件名称和未删除文件名称一致
        for (CoCloudUserFile record : records) {
            QueryWrapper queryWrapper = Wrappers.query();
            queryWrapper.eq("user_id", context.getUserId());
            queryWrapper.eq("parent_id", record.getParentId());
            queryWrapper.eq("filename", record.getFilename());
            queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
            if (iUserFileService.count() > 0) {
                throw new CoCloudBusinessException("文件还原失败，该还原文件中存在同名文件，请修改");
            }
        }
    }

    /**
     * 检查文件还原的操作权限
     *
     * @param context
     */
    private void checkRestorePermission(RestoreContext context) {
        // 获取context中的文件id列表
        List<Long> fileIdList = context.getFileIdList();
        // 通过文件id列表获取到userFile
        List<CoCloudUserFile> records = iUserFileService.listByIds(fileIdList);
        // 判空
        if (CollectionUtils.isEmpty(records)) {
            throw new CoCloudBusinessException("文件还原失败");
        }
        // 通过userFile列表获取到所有的用户id，使用set存储
        Set<Long> userIdSet = records.stream().map(CoCloudUserFile::getUserId).collect(Collectors.toSet());
        // 如果set size大于1说明用户没有权限
        if (userIdSet.size() > 1) {
            throw new CoCloudBusinessException("您无权执行文件还原");
        }
        // 如果set中的userId不包含context中的用户id说明没有权限
        if (!userIdSet.contains(context.getUserId())) {
            throw new CoCloudBusinessException("您无权执行文件还原");
        }
        // 符合条件将userFile列表记录
        context.setRecords(records);
    }


}
