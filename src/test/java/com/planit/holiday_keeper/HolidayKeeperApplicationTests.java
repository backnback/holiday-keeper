package com.planit.holiday_keeper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.management.ManagementFactory;

@SpringBootTest
class HolidayKeeperApplicationTests {

	@Test
	void contextLoads() {
		long uptimeMs = ManagementFactory.getRuntimeMXBean().getUptime();
		long minutes = uptimeMs / 60000;
		long seconds = (uptimeMs % 60000) / 1000;
		System.out.println("초기 적재 완료까지 : " + minutes + "분 " + seconds + "초");
	}

}
