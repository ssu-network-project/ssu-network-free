package com.example.domain.usedTime.api;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.dto.GetUsedTimeRes;
import com.example.domain.usedTime.service.UsedTimeService;
import com.example.global.JPacketCapture;
import com.example.global.UsageTracker;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.authenticator.jaspic.CallbackHandlerImpl;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/UsedTime")
public class UsedTimeController {

    private final UsedTimeService usedTimeService;
    private final JPacketCapture jPacketCapture;

    private final ChildRepository childRepository;

    private final TimeGoalRepository timeGoalRepository;


    //1.도메인 별 사용 시간 캡쳐 API
    /**해당 구현 서비스 로직은
     * 캡처 - global/JpacketCapture.java
     * 패킷 저장 및 업데이트 - UsedTimeService.java에
     * 나눠져 저장 되어있습니다**/
    @GetMapping("/capture/{userId}")
    public void capturePacket(@PathVariable(name = "userId") Long userIdx) throws IOException {
        jPacketCapture.startCaptureByInterface(userIdx);
    }

    //2.도메인 별 사용 시간 조회 API
    @GetMapping("/all/{userId}")
    public List<GetUsedTimeRes> getAllUsedTime(@PathVariable(name = "userId") Long userIdx){
        return usedTimeService.getAllUsedTime(userIdx);
    }

    //3.도메인 별 사용 시간 캡쳐 시작 API 멀티스레드 구현
    /**현재 도메인 별로 멀티스레드로 패킷 캡쳐 시도를 하였고, 콘솔창에 출력까지 구현, 나머지 기능과의 연동은 분가능합니다**/
    @GetMapping("/capture/multithread/{userId}")
    public void capturePacketMultiThread(@PathVariable(name="userId") Long userIdx) throws InterruptedException {
        usedTimeService.capturePacketMultiThread(userIdx);
    }

}
