package com.example.domain.usedTime.service;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.entity.UsedTime2;
import com.example.domain.usedTime.repository.UsedTimeRepository;
import com.example.domain.usedTime.repository.UsedTimeRepository2;
import com.example.global.UsageTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsedTimeService {

    private final ChildRepository childRepository;
    private final TimeGoalRepository timeGoalRepository;
    private UsageTracker usageTracker;
    private final UsedTimeRepository usedTimeRepository;
    private final UsedTimeRepository2 usedTimeRepository2;
    private final List<String> urlList = new ArrayList<>();

    public void capturePacketMultiThread(Long userIdx){

        // 인자로 들어온 Index로 Child를 찾음.
        Child child = childRepository
                .findById(userIdx)
                .get();

        // child로 TimeGoal list를 불러옴(child로 연결)
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();


        //DB에서 url만 추출해서 urlList에 저장
        for (TimeGoal timeGoal : timeGoals) {
            urlList.add(timeGoal.getDomainName());

        }

        // 실험용
//        urlList.add("naver.com");
//        urlList.add("youtube.com");
//        urlList.add("example.com");


        usageTracker = new UsageTracker(urlList);

        usageTracker.trackTime();


    }

    public void saveUsageToDB(Long userIdx) {
        // 데이터 DB에 저장하기
        HashMap<String,Long> allTime = usageTracker.getAllUsedTime();
        Child child =  childRepository.findById(userIdx).get();

        for (String url : allTime.keySet()) {
            // db의 url과 child로 저장 요소를 찾아옴
            UsedTime timeUsage = null;

            for (UsedTime usage : usedTimeRepository.findByChild(child).get()) {
                if (usage.getDomainName().equals(url)) {
                    timeUsage = usage;
                    break; // 'name'이 "naver"인 첫 번째 'Usage'를 찾았으므로 반복문을 종료합니다.
                }
            }

            if (timeUsage == null) {
                insertFirstCaptureTime(userIdx, url);

                for (UsedTime usage : usedTimeRepository.findByChild(child).get()) {
                    if (usage.getDomainName().equals(url)) {
                        timeUsage = usage;
                        break; // 'name'이 "naver"인 첫 번째 'Usage'를 찾았으므로 반복문을 종료합니다.
                    }
                }
            }

            if(timeUsage != null) {
                UsedTime updatedUsedTime = UsedTime.builder()
                        .id(timeUsage.getId())
                        .usedTime(allTime.get(url))
                        .child(timeUsage.getChild())
                        .domainName(timeUsage.getDomainName())
                        .build();
                usedTimeRepository.save(updatedUsedTime);
            } else
                System.out.println("db에 존재하지 않는 column입니다");
        }
    }

    public void insertCaptureTime(long userIdx, String url) {

        UsedTime firstUsedTime = UsedTime.builder()
                .usedTime(0L)
                .domainName(url)
                .child(childRepository.findById(userIdx).get())
                .build();
        usedTimeRepository.save(firstUsedTime);
    }
    public void insertFirstCaptureTime(long userIdx, String url) {

        UsedTime firstUsedTime = UsedTime.builder()
                .usedTime(0L)
                .domainName(url)
                .child(childRepository.findById(userIdx).get())
                .build();
        usedTimeRepository.save(firstUsedTime);
    }

    /* 이전 구현 내용 */
    public void insertFirstCaptureTimeJPacketCapture(Child child, String ipAddress, String domainName, Long firstTimeCapturedTime) {

        UsedTime2 firstUsedTime = UsedTime2.builder()
                .usedTime(0L)
                .firstCapturedTime(firstTimeCapturedTime)
                .child(child)
                .capturedNum(1)
                .domainName(domainName)
                .ipAddress(ipAddress)
                .build();
        usedTimeRepository2.save(firstUsedTime);
    }
    public void insertCaptureTimeJPacketCapture(Child child, UsedTime2 existedUsedTime, Long usedTime, String extractedIpAddress, String domainName){
        System.out.println("UsedTime updated in DB " + domainName);
        Long firstCapturedTime = existedUsedTime.getFirstCapturedTime();
        //2번 update
        Long id = existedUsedTime.getId();
        int capturedNum = existedUsedTime.getCapturedNum();
        UsedTime2 existedUsedTimeForUpdate = usedTimeRepository2.findById(id).get();
        capturedNum +=1;

        UsedTime2 updatedUsedTime = UsedTime2.builder()
                .id(id)
                .usedTime(usedTime)
                .firstCapturedTime(firstCapturedTime)
                .child(child)
                .capturedNum(capturedNum)
                .domainName(domainName)
                .ipAddress(extractedIpAddress)
                .build();
        usedTimeRepository2.save(updatedUsedTime);
    }
}



