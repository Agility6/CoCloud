package com.coCloud.server.common.schedule.launcher;

import com.coCloud.schedule.ScheduleManager;
import com.coCloud.server.common.schedule.task.CleanExpireChunkFileTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * ClassName: CleanExpireFileChunkTaskLauncher
 * Description: 定时清理过期文件分片任务触发器
 *
 * @Author agility6
 * @Create 2024/6/6 22:46
 * @Version: 1.0
 */
@Component
public class CleanExpireFileChunkTaskLauncher implements CommandLineRunner {

    private final static String CRON = "1 0 0 * * ? ";

    @Autowired
    private CleanExpireChunkFileTask task;

    @Autowired
    private ScheduleManager scheduleManager;

    @Override
    public void run(String... args) throws Exception {
        scheduleManager.startTask(task, CRON);
    }
}
