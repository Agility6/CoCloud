package com.coCloud.bloom.fliter.local.test;

import com.coCloud.bloom.filter.core.BloomFilter;
import com.coCloud.bloom.filter.local.LocalBloomFilterManager;
import com.coCloud.core.constants.CoCloudConstants;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ClassName: LocalBloomFilterTest
 * Description:
 *
 * @Author agility6
 * @Create 2024/8/3 15:33
 * @Version: 1.0
 */
@SpringBootTest(classes = LocalBloomFilterTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootApplication(scanBasePackages = CoCloudConstants.BASE_COMPONENT_SCAN_PATH + ".bloom.filter.local")
@Slf4j
public class LocalBloomFilterTest {

    @Autowired
    private LocalBloomFilterManager manager;

    @Test
    public void localBloomFilterTest() {
        BloomFilter<Integer> bloomFilter = manager.getFilter("test");
        long failNum = 0L;
        for (int i = 0; i < 1000000; i++) {
            bloomFilter.put(i);
        }

        for (int j = 1000000; j < 1100000; j++) {
            boolean result = bloomFilter.mightContain(j);
            if (result) {
                failNum++;
            }
        }

        log.info("test num {}, fail num {}", 100000, failNum);

    }
}
