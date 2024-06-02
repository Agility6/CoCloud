package com.coCloud.server.common.listenner.search;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.coCloud.core.utils.IdUtil;
import com.coCloud.server.common.event.search.UserSearchEvent;
import com.coCloud.server.modules.user.entity.CoCloudUserSearchHistory;
import com.coCloud.server.modules.user.service.IUserSearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.DuplicateFormatFlagsException;

/**
 * ClassName: UserSearchEventListener
 * Description:
 *
 * @Author agility6
 * @Create 2024/6/2 21:16
 * @Version: 1.0
 */
@Component
public class UserSearchEventListener {

    @Autowired
    private IUserSearchHistoryService iUserSearchHistoryService;

    /**
     * 监听用户搜索事件，将其保存到用户的搜素历史记录当中
     *
     * @param event
     */
    @EventListener(classes = UserSearchEvent.class)
    public void saveSearchHistory(UserSearchEvent event) {
        CoCloudUserSearchHistory record = new CoCloudUserSearchHistory();

        record.setId(IdUtil.get());
        record.setUserId(event.getUserId());
        record.setSearchContent(event.getKeyword());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());

        try {
            iUserSearchHistoryService.save(record);
        } catch (DuplicateKeyException e) {
            // 表示更新
            UpdateWrapper updateWrapper = Wrappers.update();
            updateWrapper.eq("user_id", event.getUserId());
            updateWrapper.eq("search_content", event.getKeyword());
            updateWrapper.set("update_time", new Date());
            iUserSearchHistoryService.update(updateWrapper);
        }
    }
}
