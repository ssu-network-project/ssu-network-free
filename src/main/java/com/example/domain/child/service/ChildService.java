package com.example.domain.child.service;

import com.example.domain.child.dto.PostChildReq;
import com.example.domain.child.dto.PostChildRes;
import com.example.domain.child.dto.RemainTimeRes;
import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.parent.entity.Parent;
import com.example.domain.parent.repository.ParentRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.repository.UsedTimeRepository;
import com.example.global.JPacketCapture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository childRepository;

    private final TimeGoalRepository timeGoalRepository;

    private final UsedTimeRepository usedTimeRepository;

    private HashMap<String, Long> timeOffsetByUrl = new HashMap<>();

    private Long allowance;

    private static final long MILLIS_IN_SECOND = 1000;

    private static final long SECONDS_IN_MINUTE = 60;


    /**
     *1.회원가입 서비스 로직 구현
     */
    public PostChildRes createUser(PostChildReq postUserReq){
        Child child = postUserReq.toEntity();
        childRepository.save(child);
        return PostChildRes.of(child);
    }


    /**
     *2.도메인 별 목표 시간 대비 남은 시간 계산
     */
    public List<RemainTimeRes> getRemainTimeGoal(Long userIdx){
        Child child = childRepository.findById(userIdx).get();
        //초기에 등록한 도메인별 목표 시간과 현재 저장된 목표 도메인 별 사용시간을 리스트 형식으로 불러온다.
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();
        List<UsedTime> usedTimes = usedTimeRepository.findByChild(child).get();
        List<RemainTimeRes> remainTimeResList = new ArrayList<>();

        //만약 도메인이 같다면, 남은 시간을 계산한다.
        for (TimeGoal timeGoal : timeGoals) {
            for (UsedTime usedTime : usedTimes) {
                if(timeGoal.getDomainName() == usedTime.getDomainName()){
                    //남은 시간 (단위 : 초)
                    long remainTime = timeGoal.getTime() * SECONDS_IN_MINUTE - usedTime.getUsedTime() / MILLIS_IN_SECOND;
                    //남은 시간 (단위 : 초)의 단위를 (단위 : 분 초)로 변환
                    long remainMinutes = remainTime / SECONDS_IN_MINUTE;
                    long remainSeconds = remainTime - remainMinutes * SECONDS_IN_MINUTE;
                    remainTimeResList.add(RemainTimeRes.of(timeGoal,remainMinutes, remainSeconds));
                }
            }
        }
        return remainTimeResList;
    }

    /**
     *3.현재 받을 용돈 조회 API
     */
    public Long getAllowance(Long userIdx){
        Child child = childRepository.findById(userIdx).get();
        //초기 용돈 조회
        allowance =  child.getAllowance();

        //초기에 등록한 도메인별 목표 시간과 현재 저장된 목표 도메인 별 사용시간을 리스트 형식으로 불러온다.
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();
        List<UsedTime> usedTimes = usedTimeRepository.findByChild(child).get();

        for (TimeGoal timeGoal : timeGoals) {
            for (UsedTime usedTime : usedTimes) {
                if(timeGoal.getDomainName() == usedTime.getDomainName()){
                    //만약 도메인이 같다면, 남은 시간을 계산한다. (단위 : 초)
                    long timeOffset = timeGoal.getTime() * SECONDS_IN_MINUTE - usedTime.getUsedTime() / MILLIS_IN_SECOND;;
                    //(도메인, 시간 차이)를 리스트 HashMap에 저장한다.
                    timeOffsetByUrl.put(timeGoal.getDomainName(),timeOffset);
                    //1초당 10원의 용돈 증가를 한다.
                    //자녀의 자율성을 위해 용돈 차감 로직은 구현하지 않으며, 목표 시간이 다 소진되면 용돈 증가가 되지 않는다.
                    allowance += timeOffset * 10;
                }
            }
        }
        return allowance;
    }
}
