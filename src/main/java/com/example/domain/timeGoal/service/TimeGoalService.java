package com.example.domain.timeGoal.service;

import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.dto.PostTimeGoalReq;
import com.example.domain.timeGoal.dto.PostTimeGoalRes;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TimeGoalService {

    private final ChildRepository childRepository;
    private final TimeGoalRepository timeGoalRepository;



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
}
