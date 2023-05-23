package com.example.domain.usedTime.dto;


import com.example.domain.usedTime.entity.UsedTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class GetUsedTimeRes {

    private Long usedTimeId;
    private Long childId;

    private Long usedTime;

    private String domainName;


    public static GetUsedTimeRes of(UsedTime usedTime){
        return GetUsedTimeRes.builder()
                .usedTimeId(usedTime.getId())
                .childId(usedTime.getChild().getId())
                .usedTime(usedTime.getUsedTime())
                .domainName(usedTime.getDomainName())
                .build();
    }
}
