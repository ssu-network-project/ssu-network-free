package com.example.domain.timeGoal.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**도메인 별 시간 목표 생성 API의 Request Body**/

/**
 * 도메인
 * 목표 시간 (단위 : 분)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTimeGoalReq {
    private String domainName;
    private Long time;


    @Builder
    public PostTimeGoalReq(String domainName, Long time){
        this.domainName = domainName;
        this.time = time;
    }

    public PostTimeGoalReq toEntity(){
        return PostTimeGoalReq.builder()
                .domainName(domainName)
                .time(time)
                .build();
    }
}
