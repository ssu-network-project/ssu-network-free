package com.example.domain.usedTime.service;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.repository.UsedTimeRepository;
import com.example.global.JPacketCapture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsedTimeService {

    private final UsedTimeRepository usedTimeRepository;

    private final ChildRepository childRepository;

    //도메인에 대한 패킷 저장 업데이트
    public void insertUsedTimeByDomain(Child child, UsedTime existedUsedTime, Long usedTime, String extractedIpAddress, String domainName){
        System.out.println("UsedTime updated in DB " + domainName);
        Long firstCapturedTime = existedUsedTime.getFirstCapturedTime();
        //2번 update
        Long id = existedUsedTime.getId();
        int capturedNum = existedUsedTime.getCapturedNum();
        UsedTime existedUsedTimeForUpdate = usedTimeRepository.findById(id).get();
        capturedNum +=1;

        UsedTime updatedUsedTime = UsedTime.builder()
                .id(id)
                .usedTime(usedTime)
                .firstCapturedTime(firstCapturedTime)
                .child(child)
                .capturedNum(capturedNum)
                .domainName(domainName)
                .ipAddress(extractedIpAddress)
                .build();
        usedTimeRepository.save(updatedUsedTime);
    }


    //해당 도메인에 관련딘 패킷 첫 저장
    public void insertFirstCaptureTime(Child child, String ipAddress, String domainName, Long firstTimeCapturedTime) {

        UsedTime firstUsedTime = UsedTime.builder()
                .usedTime(0L)
                .firstCapturedTime(firstTimeCapturedTime)
                .child(child)
                .capturedNum(1)
                .domainName(domainName)
                .ipAddress(ipAddress)
                .build();
        usedTimeRepository.save(firstUsedTime);
    }

//    public void insertNaverUsedTime(Long userIdx, Long usedTime){
//
//        Child child = childRepository.findById(userIdx).get();
//
//        UsedTime newUsedTime = UsedTime.builder()
//                .usedTime(usedTime)
//                .child(child)
//                .domainName("www.naver.com")
//                .ipAddress("223.130")
//                .build();
//
//
//        if(usedTimeRepository.findByChild(child).isEmpty()){
//            //Child 없으면 무조건 새로 저장
//            usedTimeRepository.save(newUsedTime);
//        }
//        else{
//            //Child로 조회했을 때,
//            //1. 도메인 네임 다르면 => 무조건 저장
//            //2. 도메인 네임 같은 게 존재하면 => 업데이트
//            int domainCount=0;
//            List<UsedTime> usedTimeWithDomains = usedTimeRepository.findByChild(child);
//            for (UsedTime usedTimeWithDomain : usedTimeWithDomains) {
//                if(usedTimeWithDomain.getDomainName()=="www.naver.com"){
//                    domainCount ++;
//                    //2번 update
//                    Long id = usedTimeWithDomain.getId();
//                    UsedTime usedTimeByNaver = usedTimeRepository.findById(id).get();
//                    UsedTime updatedUsedTime = UsedTime.builder()
//                            .id(id)
//                            .child(child)
//                            .domainName("www.naver.com")
//                            .ipAddress("223.130")
//                            .usedTime(usedTime)
//                            .build();
//                    usedTimeRepository.save(updatedUsedTime);
//                }
//            }
//            if(domainCount==0){
//                //1번 insert
//                usedTimeRepository.save(newUsedTime);
//            }
//        }
//    }
}
