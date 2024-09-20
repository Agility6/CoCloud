package com.coCloud.server.common.schedule.launcher;

import com.coCloud.schedule.ScheduleManager;
import com.coCloud.server.common.schedule.task.CleanExpireChunkFileTask;
import com.coCloud.server.common.schedule.task.RebuildShareSimpleDetailBloomFilterTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * ClassName: RebuildShareSimpleDetailBloomFilterTaskLauncher
 * Description: 定时重建布隆过滤器
 *
 * @Author agility6
 * @Create 2024/6/6 22:46
 * @Version: 1.0
 */
@Component
public class RebuildShareSimpleDetailBloomFilterTaskLauncher implements CommandLineRunner {

    private final static String CRON = "1 0 0 * * ? ";

    @Autowired
    private RebuildShareSimpleDetailBloomFilterTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }
}
