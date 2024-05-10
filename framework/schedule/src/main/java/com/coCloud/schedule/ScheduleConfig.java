package com.coCloud.schedule;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * ClassName: ScheduleConfig
 * Description: 定时模块配置类
 *
 * @Author agility6
 * @Create 2024/5/10 21:47
 * @Version: 1.0
 */
@SpringBootConfiguration
public class ScheduleConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
}
