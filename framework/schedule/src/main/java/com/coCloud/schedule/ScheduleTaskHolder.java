package com.coCloud.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * ClassName: ScheduleTaskHolder
 * Description: 定时任务和定时任务结果的缓存对象
 *
 * @Author agility6
 * @Create 2024/5/10 21:48
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTaskHolder implements Serializable {

    /**
     * 执行任务实体
     */
    private ScheduleTask scheduleTask;

    /**
     * 执行任务的结果实体
     */
    private ScheduledFuture scheduledFuture;
}
