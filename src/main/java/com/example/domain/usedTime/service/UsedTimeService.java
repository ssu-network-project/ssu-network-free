package com.example.domain.usedTime.service;


import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.dto.GetUsedTimeRes;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.repository.UsedTimeRepository;
import com.example.global.UsageTracker;
import lombok.RequiredArgsConstructor;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class UsedTimeService {

    private final UsedTimeRepository usedTimeRepository;

    private final ChildRepository childRepository;

    private final TimeGoalRepository timeGoalRepository;

    private HashMap<String, UsageTracker.Pair<Pcap,Long>> usageList = new HashMap<>();;
    // <url , <pcap,usage>> = new HashMap<>();

    private List<String> urlList = new ArrayList<>();

    private static final String HTTPS_SYN_FIN_FILTER = "dst port 443 and (tcp-syn|tcp-fin) != 0";
    private static final long SECONDS_IN_MINUTE = 60;
    private static final long MILLIS_IN_SECOND = 1000;
    public void capturePacketMultiThread(Long userIdx){
        Child child = childRepository.findById(userIdx).get();
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();

        //DB에서 UsedTime 가져와서 url만 추출해서 urlList에 저장
        for (TimeGoal timeGoal : timeGoals) {
            urlList.add(timeGoal.getDomainName()); 
        }
        makeusageList();
    }
    public void makeusageList(){

        StringBuilder errbuf = new StringBuilder();
        List<PcapIf> devices = new ArrayList<>();
        int result = Pcap.findAllDevs( devices, errbuf);
        if (result != Pcap.OK) {
            System.err.println("네트워크 장치를 찾을 수 없습니다: " + errbuf);
            return;
        }

        // 캡처 장치 열기
        int snaplen = 64 * 1024; // 패킷 캡처 크기
        int flags = Pcap.MODE_PROMISCUOUS; // 모든 패킷 캡처
        int timeout = 10 * 1000; // 타임아웃(ms)

        PcapIf pcapIf = devices.get(1);

        for(String url : urlList) {

            Long time = 0L;
            // 여기 pcap만들어서 url List 만들어줘야 함.

            String FILTER = "host " + url + " and " + HTTPS_SYN_FIN_FILTER;

            System.out.println("filter : " + FILTER);

            Pcap pcap = Pcap.openLive(pcapIf.getName(), snaplen, flags, timeout, errbuf);
            if (pcap == null) {
                System.err.println("캡처 장치를 열 수 없습니다: " + errbuf);
                return;
            }
            // 패킷 필터 설정
            PcapBpfProgram filter = new PcapBpfProgram();
            result = pcap.compile(filter, FILTER, 0, 0);
            if (result != Pcap.OK) {
                System.err.println("패킷 필터를 설정할 수 없습니다: " + pcap.getErr());
                return;
            }
            result = pcap.setFilter(filter);
            if (result != Pcap.OK) {
                System.err.println("패킷 필터를 적용할 수 없습니다: " + pcap.getErr());
                return;
            }

            usageList.put(url, new UsageTracker.Pair<>(pcap, time));
        }
        trackTime();
    }

    public void trackTime() {

        ExecutorService executorService = Executors.newFixedThreadPool(usageList.size());

        for(String url : usageList.keySet()) {
            executorService.execute(() -> {
                Pcap pcap = usageList.get(url).left;
                long time = 0;

                // 사용 시간 추적
                long startTime = 0;

                System.out.println("식별 시작!");

                while (true) {
                    PcapPacket packet = new PcapPacket(0);
                    if (pcap.nextEx(packet) != Pcap.NEXT_EX_OK) {
                        continue;
                    }

                    // YouTube 패킷 식별
                    Ip4 ip4 = packet.getHeader(new Ip4());
                    Ip6 ip6 = packet.getHeader(new Ip6());

                    String destinationIp = null;

                    if (ip4 != null) {
                        destinationIp = FormatUtils.ip(ip4.destination());
                        //System.out.println("Ip4 주소 캡처");
                    } else if (ip6 != null) {
                        destinationIp = FormatUtils.ip(ip6.destination());
                        //System.out.println("Ip6 주소 캡처");
                    } else {
                        System.out.println("목적지 IP를 찾지 못했습니다 ㅠㅠ");
                    }

                    System.out.println(destinationIp);

                    long currentTime = packet.getCaptureHeader().timestampInMillis();

                    if (startTime == 0) {
                        startTime = currentTime;
                    } else if ((currentTime - startTime)/(MILLIS_IN_SECOND*SECONDS_IN_MINUTE) > 1) {
                        startTime = currentTime;
                    } else {
                        long endTime = currentTime;
                        time += (endTime - startTime);
                        startTime = endTime;
                    }

                    // 결과 표시 (예: 매분마다)
                    long elapsedSeconds = time / MILLIS_IN_SECOND;
                    long minutes = elapsedSeconds / SECONDS_IN_MINUTE;
                    long seconds = elapsedSeconds % SECONDS_IN_MINUTE;

                    System.out.println("사용자의 " + url + " 사용 시간: " + minutes + "분 " + seconds + "초");

                   usageList.get(url).right = time;
                }
            });
        }
        // 캡처 장치 닫기
    }




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

    public List<GetUsedTimeRes> getAllUsedTime(Long userIdx){
        Child child = childRepository.getById(userIdx);
        List<UsedTime> usedTimeList = usedTimeRepository.findByChild(child).get();
        List<GetUsedTimeRes> getUsedTimeResList = new ArrayList<>();
        for (UsedTime usedTime : usedTimeList) {
            GetUsedTimeRes getUsedTime = GetUsedTimeRes.of(usedTime);
            getUsedTimeResList.add(getUsedTime);
        }
        return getUsedTimeResList;
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
private static class Pair<L,R>{
    L left;
    R right;

    Pair(L l, R r){
        this.left=l;
        this.right=r;
    }
}

}



