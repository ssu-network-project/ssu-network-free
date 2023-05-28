package com.example.domain.usedTime.service;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.dto.GetUsedTimeRes;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.entity.UsedTime2;
import com.example.domain.usedTime.repository.UsedTimeRepository;
import com.example.domain.usedTime.repository.UsedTimeRepository2;
import com.example.global.UsageTracker;
import lombok.RequiredArgsConstructor;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

@Service
@RequiredArgsConstructor
public class UsedTimeService {

    private final UsedTimeRepository usedTimeRepository;

    private final ChildRepository childRepository;

    private final TimeGoalRepository timeGoalRepository;

    private List<String> urlList = new ArrayList<>();

    public void capturePacketMultiThread(Long userIdx) throws InterruptedException {
        UsageTracker usageTracker = getUsageTracker(userIdx);

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

    private UsageTracker getUsageTracker(Long userIdx) {
        Child child = childRepository.findById(userIdx).get();
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();


        //DB에서 UsedTime 가져와서 url만 추출해서 urlList에 저장
        for (TimeGoal timeGoal : timeGoals) {
            urlList.add(timeGoal.getDomainName());
        }

        UsageTracker usageTracker = new UsageTracker(urlList);
        return usageTracker;
    }

    /**
     *1.도메인 별 사용 시간 캡쳐 API
     */
    //도메인에 대한 패킷 저장 업데이트
    public void insertUsedTimeByDomain(Child child, UsedTime existedUsedTime, Long usedTime, String extractedIpAddress, String domainName){
        System.out.println("UsedTime updated in DB " + domainName);
        Long firstCapturedTime = existedUsedTime.getFirstCapturedTime();
        //2번 update
        Long id = existedUsedTime.getId();
        int capturedNum = existedUsedTime.getCapturedNum();
        UsedTime existedUsedTimeForUpdate = usedTimeRepository.findById(id).get();
        capturedNum +=1;

        UsedTime updatedUsedTime = UsedTime.builder()
                .id(id)
                .usedTime(usedTime)
                .firstCapturedTime(firstCapturedTime)
                .child(child)
                .capturedNum(capturedNum)
                .domainName(domainName)
                .ipAddress(extractedIpAddress)
                .build();
        usedTimeRepository.save(updatedUsedTime);
    }


    /**
     *1.도메인 별 사용 시간 캡쳐 API
     */
    //해당 도메인의 첫 패킷 저장
    public void insertFirstCaptureTime(Child child, String ipAddress, String domainName, Long firstTimeCapturedTime) {

        UsedTime firstUsedTime = UsedTime.builder()
                .usedTime(0L)
                .firstCapturedTime(firstTimeCapturedTime)
                .child(child)
                .capturedNum(1)
                .domainName(domainName)
                .ipAddress(ipAddress)
                .build();
        usedTimeRepository.save(firstUsedTime);
    }


    /**
     *2.도메인 별 사용 시간 조회 API
     */
    public List<GetUsedTimeRes> getAllUsedTime(Long userIdx){
        Child child = childRepository.getById(userIdx);
        List<UsedTime> usedTimeList = usedTimeRepository.findByChild(child).get();
        List<GetUsedTimeRes> getUsedTimeResList = new ArrayList<>();
        for (UsedTime usedTime : usedTimeList) {
            GetUsedTimeRes getUsedTime = GetUsedTimeRes.of(usedTime);
            getUsedTimeResList.add(getUsedTime);
        }
        return getUsedTimeResList;
    }
}



