package com.example.domain.timeGoal.dto;

import com.example.domain.timeGoal.entity.TimeGoal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**현재까지 저장된 모든 도메인 별 시간 목표 조회 Response Body**/
/**하나의 도메인 별 시간 **/

/**
 * 목표 id
 * 도메인
 * 시간 (단위 : 분)
 * 등록한 자녀 id
 */
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
