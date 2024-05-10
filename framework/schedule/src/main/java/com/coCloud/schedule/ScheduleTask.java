package com.coCloud.schedule;

/**
 * ClassName: ScheduleTask
 * Description: 定时任务的任务接口
 *
 * @Author agility6
 * @Create 2024/5/10 21:47
 * @Version: 1.0
 */
public interface ScheduleTask extends Runnable {

    /**
     * 获取定时任务的名称
     *
     * @return
     */
    String getName();

}