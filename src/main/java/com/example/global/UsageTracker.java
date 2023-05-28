package com.example.global;

import com.example.domain.child.entity.Child;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.entity.UsedTime2;
import com.example.domain.usedTime.repository.UsedTimeRepository2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.network.Ip6;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Component
@RequiredArgsConstructor
public class UsageTracker {
    private static final String HTTPS_SYN_FIN_FILTER = "dst port 443 and (tcp-syn|tcp-fin) != 0";
    private static final long SECONDS_IN_MINUTE = 60;
    private static final long MILLIS_IN_SECOND = 1000;


    private static HashMap<String, Pair<Pcap,Long>> usageList;
    // <url , <pcap,usage>>


    public static HashMap<String, Long> getUsageList() {

        HashMap<String,Long> list = new HashMap<>();

        for(String url : usageList.keySet()) {
            list.put(url,usageList.get(url).right);
        }

        return list;
    }


    /**3.도메인 별 사용 시간 캡쳐 시작 API 멀티스레드 구현 로직*/
    public UsageTracker(List<String> urlList) {

        usageList = new HashMap<>();

        // 네트워크 장치 목록 가져오기
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

            usageList.put(url, new Pair<>(pcap, time));
        }
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

    public static void viewUsage() {

        int count = 1;

        for(String url : usageList.keySet()) {

            long time = usageList.get(url).right;

            long elapsedSeconds = time / MILLIS_IN_SECOND;
            long minutes = elapsedSeconds / SECONDS_IN_MINUTE;
            long seconds = elapsedSeconds % SECONDS_IN_MINUTE;

            System.out.println(count++ + ") " + url);
            System.out.println("- usage : "+ minutes + "분 " + seconds + "초");
        }
    }

    public static class Pair<L,R>{
        public L left;
        public R right;

        public Pair(L l, R r){
            this.left=l;
            this.right=r;
        }
    }
}

