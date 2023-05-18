package com.example.domain.timeGoal.dto;

import com.example.domain.timeGoal.entity.TimeGoal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTimeGoalRes{
    private String domainName;
    private Long time;


    @Builder
    public PostTimeGoalRes(String domainName, Long time){
        this.domainName = domainName;
        this.time = time;
    }

    public PostTimeGoalReq of(TimeGoal timeGoal){
        return PostTimeGoalReq.builder()
                .domainName(domainName)
                .time(time)
                .build();
    }
}
