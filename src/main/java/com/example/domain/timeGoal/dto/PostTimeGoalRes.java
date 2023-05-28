package com.example.domain.timeGoal.dto;

import com.example.domain.timeGoal.entity.TimeGoal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**도메인 별 시간 목표 생성 API의 Response Body**/

/**
 * 시간 목표 id
 * 도메인
 * 목표 시간 (단위 : 분)
 * 성공 메시지
 */
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
