package com.example.FreeNetworkProject;

import com.example.global.UsageTracker;
import org.jboss.jandex.TypeTarget;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SpringBootTest
class FreeNetworkProjectApplicationTests {

	@Test
	void contextLoads() {

		List<String> urlList = new ArrayList<>();

		urlList.add("naver.com");
		urlList.add("youtube.com");
		urlList.add("example.com");

		UsageTracker usageTracker = new UsageTracker(urlList);

		usageTracker.trackTime();

		// 10초에 한번씩 usage 출력하기
		// 실시간으로 출력됨.
		Timer timer = new Timer();

		// TimerTask를 상속한 클래스를 생성합니다.
		TimerTask task = new TimerTask() {
			@Override
			public void run() {

				usageTracker.viewUsage();
			}
		};

		// 10초마다 작업(task)을 반복 실행합니다.
		timer.schedule(task, 0, 10000);
	}

}
