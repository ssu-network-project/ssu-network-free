package com.example.domain.timeGoal.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostTimeGoals {
    private String domainName;
    private Long time;


    @Builder
    public PostTimeGoals(String domainName, Long time){
        this.domainName = domainName;
        this.time = time;
    }

    public PostTimeGoals toEntity(){
        return PostTimeGoals.builder()
                .domainName(domainName)
                .time(time)
                .build();
    }
}
