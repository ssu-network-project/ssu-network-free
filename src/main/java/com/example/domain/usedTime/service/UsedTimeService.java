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


    public void insertNaverUsedTime(Long userIdx, Long usedTime){

        Child child = childRepository.findById(userIdx).get();

        UsedTime newUsedTime = UsedTime.builder()
                .usedTime(usedTime)
                .child(child)
                .domainName("www.naver.com")
                .ipAddress("223.130")
                .build();


        if(usedTimeRepository.findByChild(child).isEmpty()){
            //Child 없으면 무조건 새로 저장
            usedTimeRepository.save(newUsedTime);
        }
        else{
            //Child로 조회했을 때,
            //1. 도메인 네임 다르면 => 무조건 저장
            //2. 도메인 네임 같은 게 존재하면 => 업데이트
            int domainCount=0;
            List<UsedTime> usedTimeWithDomains = usedTimeRepository.findByChild(child);
            for (UsedTime usedTimeWithDomain : usedTimeWithDomains) {
                if(usedTimeWithDomain.getDomainName()=="www.naver.com"){
                    domainCount ++;
                    //2번 update
                    Long id = usedTimeWithDomain.getId();
                    UsedTime usedTimeByNaver = usedTimeRepository.findById(id).get();
                    UsedTime updatedUsedTime = UsedTime.builder()
                            .id(id)
                            .child(child)
                            .domainName("www.naver.com")
                            .ipAddress("223.130")
                            .usedTime(usedTime)
                            .build();
                    usedTimeRepository.save(updatedUsedTime);
                }
            }
            if(domainCount==0){
                //1번 insert
                usedTimeRepository.save(newUsedTime);
            }
        }
    }
}
