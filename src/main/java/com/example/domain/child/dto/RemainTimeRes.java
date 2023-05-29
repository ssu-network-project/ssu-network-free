package com.example.domain.child.dto;

import com.example.domain.timeGoal.entity.TimeGoal;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**도메인 별 목표 시간 대비 남은 시간 조회 API의 Response Body**/

/**
 * 도메인 주소
 * 남은 시간(분)
 * 남은 시간(초)
 */
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class RemainTimeRes {

    String domainName;

    Long remainMinutes;

    Long remainSeconds;
    public static RemainTimeRes of(TimeGoal timeGoal, Long remainMinutes, Long remainSeconds){
        return RemainTimeRes.builder()
                .domainName(timeGoal.getDomainName())
                .remainMinutes(remainMinutes)
                .remainSeconds(remainSeconds)
                .build();
    }
}
