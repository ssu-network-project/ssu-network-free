package com.example.domain.usedTime.dto;


import com.example.domain.usedTime.entity.UsedTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**도메인 별 사용 시간 조회 Response Body**/
/**하나의 도메인 별 사용 시간 **/

/**
 * 사용시간 id
 * 등록 자녀 id
 * 사용 시간 (단위 : 밀리세컨드)
 * 도메인
 */

@Getter
@Setter
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
