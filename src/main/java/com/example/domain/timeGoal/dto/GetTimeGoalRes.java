package com.example.domain.timeGoal.dto;

import com.example.domain.timeGoal.entity.TimeGoal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Builder(access = AccessLevel.PRIVATE)
@Getter
public class GetTimeGoalRes {
    private Long goalId;

    private String domainName;

    private Long time;

    private Long childId;

    public static GetTimeGoalRes of(TimeGoal timeGoal){
        return GetTimeGoalRes.builder()
                .goalId(timeGoal.getId())
                .time(timeGoal.getTime())
                .domainName(timeGoal.getDomainName())
                .childId(timeGoal.getChild().getId())
                .build();
    }
}
