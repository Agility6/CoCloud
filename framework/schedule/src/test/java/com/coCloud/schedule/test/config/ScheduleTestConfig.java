package com.coCloud.schedule.test.config;

import com.coCloud.core.constants.CoCloudConstants;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * ClassName: ScheduleTestConfig
 * Description:
 *
 * @Author agility6
 * @Create 2024/5/10 22:05
 * @Version: 1.0
 */
@SpringBootConfiguration
@ComponentScan(CoCloudConstants.BASE_COMPONENT_SCAN_PATH + ".schedule")
public class ScheduleTestConfig {
}