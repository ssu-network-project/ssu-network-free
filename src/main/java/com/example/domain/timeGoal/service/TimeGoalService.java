package com.example.domain.timeGoal.service;

import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.dto.GetTimeGoalRes;
import com.example.domain.timeGoal.dto.PostTimeGoalReq;
import com.example.domain.timeGoal.dto.PostTimeGoalRes;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TimeGoalService {

    private final ChildRepository childRepository;
    private final TimeGoalRepository timeGoalRepository;


    /**
     *1.도메인 별 시간 목표 생성 API
     */
    public PostTimeGoalRes createGoalsByDomainName(PostTimeGoalReq postTimeGoals, Long userIdx) {
        System.out.println("postTimeGoals = " + postTimeGoals.getDomainName());
        System.out.println("postTimeGoals = " + postTimeGoals.getTime());
        Child child = childRepository.findById(userIdx).get();
        TimeGoal timeGoalsByUserIdx = TimeGoal.builder()
                .domainName(postTimeGoals.getDomainName())
                .time(postTimeGoals.getTime())
                .child(child)
                .build();
        TimeGoal savedGoal = timeGoalRepository.save(timeGoalsByUserIdx);
        return PostTimeGoalRes.of(savedGoal);
    };


    /**
     *2.현재까지 저장된 모든 도메인 별 시간 목표 조회
     */
    public List<GetTimeGoalRes> getAllGoalsByUser(Long userIdx){
        Child child = childRepository.findById(userIdx).get();
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();
        List<GetTimeGoalRes> getTimeGoalResList = new ArrayList<>();
        for (TimeGoal timeGoal : timeGoals) {
            GetTimeGoalRes getTimeGoalRes = GetTimeGoalRes.of(timeGoal);
            getTimeGoalResList.add(getTimeGoalRes);
        }
        return getTimeGoalResList;
    }
}
