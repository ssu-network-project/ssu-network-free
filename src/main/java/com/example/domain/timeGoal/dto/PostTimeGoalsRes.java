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
public class PostTimeGoalsRes {
    private Long id;

    private String domainName;

    private Long time;

    private String successMessage;

    public static PostTimeGoalsRes of(TimeGoal timeGoal){
       return PostTimeGoalsRes.builder()
               .id(timeGoal.getId())
               .time(timeGoal.getTime())
               .domainName(timeGoal.getDomainName())
               .successMessage("목표저장에 성공했습니다")
               .build();
    }
}
