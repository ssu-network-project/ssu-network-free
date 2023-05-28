package com.example.domain.usedTime.service;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.repository.UsedTimeRepository;
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
    private List<String> urlList = new ArrayList<>();

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
        urlList.add("naver.com");
        urlList.add("youtube.com");
        urlList.add("example.com");


        usageTracker = new UsageTracker(urlList);

        usageTracker.trackTime();


    }

    public void saveUsageToDB(Long userIdx) {
        // 데이터 DB에 저장하기
        HashMap<String,Long> allTime = usageTracker.getAllUsedTime();

        for (String url : allTime.keySet()) {
            UsedTime timeUsage = usedTimeRepository.findByDomainName(url);

            if(timeUsage != null) {
                UsedTime updatedUsedTime = UsedTime.builder()
                        .id(timeUsage.getId())
                        .usedTime(allTime.get(url))
                        .child(timeUsage.getChild())
                        .domainName(timeUsage.getDomainName())
                        .build();
                usedTimeRepository.save(updatedUsedTime);
            } else
                insertFirstCaptureTime(userIdx,url);
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

private static class Pair<L,R>{
    L left;
    R right;

    Pair(L l, R r){
        this.left=l;
        this.right=r;
    }
}

}



