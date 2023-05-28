package com.example.domain.timeGoal.dto;

import com.example.domain.timeGoal.entity.TimeGoal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class PostTimeGoalRes {
    private Long id;

    private String domainName;

    private Long time;

    private String successMessage;

    public static PostTimeGoalRes of(TimeGoal timeGoal){
        return PostTimeGoalRes.builder()
                .id(timeGoal.getId())
                .time(timeGoal.getTime())
                .domainName(timeGoal.getDomainName())
                .successMessage("목표저장에 성공했습니다")
                .build();
    }
}
