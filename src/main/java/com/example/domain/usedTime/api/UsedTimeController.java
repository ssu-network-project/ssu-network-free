package com.example.domain.usedTime.api;


import com.example.domain.usedTime.service.UsedTimeService;
import com.example.global.JPacketCapture;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/UsedTime")
public class UsedTimeController {

    private final UsedTimeService usedTimeService;
    private final JPacketCapture jPacketCapture;


    //0. 단일 스레드 및 IP initial로 패킷 캡처 구현
    // 도메인에서 잡히는 ip address initail까지 얻어내는 데에는 성공했으나,
    // 패킷캡처가 잘 되지 않고, 리퀘스트가 끝나지 않는 등의 문제가 발견하여,
    // 아래 멀티 Thread 방식으로 구현하였음.
    // JPacketCapture, UsedTime2 엔티티, UsedTimeService에 JPacketCapture가 붙은 메소드는
    // 해당 단일 프로세스 구현에 사용된 과거 파일들임.
    /**
     * 캡처 - global/JpacketCapture.java
     * 패킷 저장 및 업데이트 - UsedTimeService.java
     **/
    @GetMapping("/capture/{userId}")
    public void capturePacket(@PathVariable(name = "userId") Long userIdx) throws IOException {
        jPacketCapture.startCaptureByInterface(userIdx);
    }

    /**
     * 캡처 - global/UsageTracker.java
     * 패킷 저장 및 업데이트 - UsedTimeService.java
     **/
    //1.도메인 별 사용 시간 캡쳐 시작 API 멀티스레드 구현
    /**현재 도메인 별로 멀티스레드로 패킷 캡쳐 시도를 하였고, 콘솔창에 출력까지 구현, 나머지 기능과의 연동은 분가능합니다**/
    @GetMapping("/capture/multithread/{userId}")
    public void capturePacketMultiThread(@PathVariable(name="userId") Long userIdx) {
        usedTimeService.capturePacketMultiThread(userIdx);
    }

    // 2. 현재까지 측정된 Usage를 DB에 저장
    @GetMapping("/save/{userId}")
    public void saveUsageToDB(@PathVariable(name="userId") Long userIdx) {
        usedTimeService.saveUsageToDB(userIdx);
    }
}
