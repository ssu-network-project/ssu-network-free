package com.example.global;

import com.example.domain.child.entity.Child;
import com.example.domain.child.repository.ChildRepository;
import com.example.domain.timeGoal.entity.TimeGoal;
import com.example.domain.timeGoal.repository.TimeGoalRepository;
import com.example.domain.usedTime.entity.UsedTime;
import com.example.domain.usedTime.repository.UsedTimeRepository;
import com.example.domain.usedTime.service.UsedTimeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JPacketCapture {

    private Pcap pcap;
    private final ChildRepository childRepository;

    private final TimeGoalRepository timeGoalRepository;
    private final UsedTimeService usedTimeService;

    private final UsedTimeRepository usedTimeRepository;

    public void startCaptureByInterface(Long userIdx) throws IOException {

        List<PcapIf> allDevs = new ArrayList<>();
        StringBuilder errbuf = new StringBuilder();
        if (Pcap.findAllDevs(allDevs, errbuf) != Pcap.OK) {
            System.err.printf("Pcap Error: %s", errbuf);
            return;
        }
        if (allDevs.isEmpty()) {
            System.out.println("No interfaces found! Make sure WinPcap or libpcap is installed.");
            return;
        }

        // 네트워크 인터페이스 목록 조회
        int interfaceNum=0;
        for (PcapIf device : allDevs) {
            System.out.printf("[%d번]: %s [%s]\n ",interfaceNum++,device.getName(),device.getDescription());
            System.out.println("device.getAddresses() = " + device.getAddresses());
            System.out.println("device.getHardwareAddress() = " + device.getHardwareAddress());
        }

        //캡처할 인터페이스 선택
        PcapIf pcapIf = allDevs.get(1);
        System.out.println("pcap = " + pcapIf.getDescription());
        System.out.println("pcapIf.getName() = " + pcapIf.getName());
        System.out.println("pcapIf.getAddresses() = " + pcapIf.getAddresses());
        System.out.println("pcapIf.getHardwareAddress() = " + pcapIf.getHardwareAddress());

        // 패킷 캡처 시작
        Pcap pcap = Pcap.openLive(pcapIf.getName(), 65536, Pcap.MODE_PROMISCUOUS, 1000, errbuf);
        if (pcap == null) {
            System.err.printf("Pcap Error: %s", errbuf);
        }
        capturePacketByDomain(pcap,userIdx);
    }
    public void capturePacketByDomain(Pcap pcap, Long userIdx) {
        //현재 Child
        Child child = childRepository.findById(userIdx).get();
        List<TimeGoal> timeGoals = timeGoalRepository.findByChild(child).get();

        //TimeGoal에 있는 domainName 추출
        List<String> goalDomainNameList= new ArrayList<>();
        for (TimeGoal timeGoal : timeGoals) {
            goalDomainNameList.add(timeGoal.getDomainName());
        }

        //추출한 도메인 관련 필터 생성
        String filter = "tcp port 443";
        PcapBpfProgram program = new PcapBpfProgram();
        int mask = 0xffffff;
        if (pcap.compile(program, filter, 1, mask) != Pcap.OK) {
            System.err.println(pcap.getErr());
        }
        if (pcap.setFilter(program) != Pcap.OK) {
            System.err.println(pcap.getErr());
        }

        String ipAddress = null;

        //TimeGoal에 있는 domainName => IP로 변환하여 리스트에 저장
        List<String> goalIpAddressList = new ArrayList<>();
        try {
            for (String goalDomainName : goalDomainNameList) {
                goalIpAddressList.add(InetAddress.getByName(goalDomainName).getHostAddress());
            }
            //변환된 TimeGoal 도메인의 ip주소 리스트출력
            for (String showIpAddress : goalIpAddressList) {
                System.out.println("IP Address changed by domain = " + showIpAddress);
            }
        } catch (Exception e) {
            System.out.println("IP 주소를 얻는 중 오류가 발생하였습니다.");
            e.printStackTrace();
        }

        // 도메인 IP 고정된 부분 추출하기
        //ex) 223.130
        String regex = "^(\\d{1,3}\\.\\d{1,3})\\..*";
        String extractedAddr = null;
        Pattern pattern = Pattern.compile(regex);

        //TimeGoal의 IP 중 앞 두파트 추출
        List<String> extrackedIpAddressList = new ArrayList<>();
        for (String s : goalIpAddressList) {
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                extractedAddr = matcher.group(1);
                extrackedIpAddressList.add(extractedAddr);
            }
        }

        //추출된 ip address 출력
        for (String s : extrackedIpAddressList) {
            System.out.println("extraced IP address from goals = " + s);
        }

        // 캡처된 패킷 처리
        //finalExtractedAddr : 223.130
        List<String> finalExtractedAddrList = new ArrayList<>();
        for (String s : extrackedIpAddressList) {
            finalExtractedAddrList.add(s);
        }
        for (String s : finalExtractedAddrList) {
            System.out.println("final extracked ip addr = " +s );
        }

        //(추출된 ip 주소, 목표 도메인 네임) 형식의 hashmap 생성
        HashMap<String, String> goalDomainNameAndIpAddress = new HashMap<>();
        for (int i = 0; i < goalDomainNameList.size(); i++) {
            String k = finalExtractedAddrList.get(i);
            String v = goalDomainNameList.get(i);
            goalDomainNameAndIpAddress.put(k,v);
        }

        //출력
        for (String s : goalDomainNameAndIpAddress.keySet()) {
            System.out.println("final key(extracted IP) = " + s);
        }
        for (String s : goalDomainNameAndIpAddress.values()) {
            System.out.println("final value(Domain Goal)= " + s);
        }


        JPacketHandler<String> packetHandler = new JPacketHandler<>() {
            private final Tcp tcp = new Tcp();
            private final Ip4 ip = new Ip4();


            int domainPacketNum; //도메인 필터에 부합하는 몇 번째 필터인지

            Long firstPacketCapturedTime=0L;//첫 번째 패킷의 캡쳐된 시간

            @Override
            public void nextPacket(JPacket packet, String user) {
                if (packet.hasHeader(tcp) && packet.hasHeader(ip)) {
                    System.out.println("user = " + user);
                    System.out.printf("캡처 시작: %s\n 패킷의 길이: %-4d\n", new Date(packet.getCaptureHeader().timestampInMillis()),
                            packet.getCaptureHeader().caplen());

                    String srcIp = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
                    String dstIp = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());
                    System.out.println("Source IP: " + srcIp);
                    System.out.println("Destination IP: " + dstIp);

                    //목표 Domain으로 오고가는 패킷 캡쳐 및 추출
                    for (String finalExtractedAddr : finalExtractedAddrList) {
                        if (srcIp.startsWith(finalExtractedAddr) || dstIp.startsWith(finalExtractedAddr)){
                            String domainName = goalDomainNameAndIpAddress.get(finalExtractedAddr);
                            //사용시간 (단위는 우선 밀리세컨드, 추후 수정 가능)
                            //업데이트용 시간
                            Long usedTime;

                            //1.처음 저장
                            //1-1) child에 대한 UsedTime이 아예 없음
                            if(usedTimeRepository.findByChild(child).isEmpty()){
                                firstPacketCapturedTime = packet.getCaptureHeader().timestampInMillis();
                                usedTimeService.insertFirstCaptureTime(child,finalExtractedAddr,domainName,firstPacketCapturedTime);
                            }

                            //usedTime 추출
                            List<UsedTime> childUsedTime = usedTimeRepository.findByChild(child);
                            Optional<UsedTime> existedUsedTimeOptional = childUsedTime.stream()
                                    .filter(usedTime1 -> usedTime1.getIpAddress() == finalExtractedAddr)
                                    .findFirst();

                            //2. 업데이트 수행
                            //DB에 있는 UsedTime 리스트 중에 지금의 ipAddress가 있음
                            if (existedUsedTimeOptional.isPresent()) {
                                UsedTime existedUsedTime = existedUsedTimeOptional.get();
                                Long currentCapturedPacket = packet.getCaptureHeader().timestampInMillis();
                                System.out.println("currentCapturedPacket = " + currentCapturedPacket);
                                firstPacketCapturedTime = existedUsedTime.getFirstCapturedTime();
                                usedTime = currentCapturedPacket - firstPacketCapturedTime;
                                usedTimeService.insertUsedTimeByDomain(child, existedUsedTime, usedTime, finalExtractedAddr, domainName);
                            } else {
                                //1-2) child에 대한 UsedTime은 존재하나, 해당 도메인은 없음
                                firstPacketCapturedTime = packet.getCaptureHeader().timestampInMillis();
                                usedTimeService.insertFirstCaptureTime(child, finalExtractedAddr, domainName, firstPacketCapturedTime);
                            }
//
//                            int existedUsedTimeByThisDomain=0;
//                            for (UsedTime existedUsedTime : childUsedTime) {
//                                //2. 업데이트 수행
//                                //DB에 있는 UsedTime 리스트 중에 지금의 ipAddress가 있음
//                                if (existedUsedTime.getIpAddress() == finalExtractedAddr) {
//                                    existedUsedTimeByThisDomain++;
//                                    System.out.println("not first Time Captured ");
//                                    Long currentCapturedPacket = packet.getCaptureHeader().timestampInMillis();
//                                    System.out.println("currentCapturedPacket = " + currentCapturedPacket);
//                                    Long firstPacketCapturedTime = existedUsedTime.getFirstCapturedTime();
//                                    usedTime = currentCapturedPacket - firstPacketCapturedTime;
//                                    usedTimeService.insertUsedTimeByDomain(child, existedUsedTime, usedTime, finalExtractedAddr, domainName);
//                                    System.out.println("usedTime = " + usedTime);
//                                }
//                            }
//                            System.out.println("existedUsedTimeByThisDomain = " + existedUsedTimeByThisDomain);
//                            if(existedUsedTimeByThisDomain==0) {
//                                //1-2) child에 대한 UsedTime은 존재하나, 해당 도메인은 없음
//                                firstPacketCapturedTime = packet.getCaptureHeader().timestampInMillis();
//                                usedTimeService.insertFirstCaptureTime(child, finalExtractedAddr, domainName, firstPacketCapturedTime);
//                            }

                            byte[] data = packet.getByteArray(tcp.getOffset(), tcp.getHeaderLength() + tcp.getPayloadLength());
                            System.out.println("packet = " + packet);
                            long time = packet.getCaptureHeader().timestampInMillis();

                            if (tcp.destination() == 443) {
                                // SSL/TLS 핸드셰이크 패킷 처리
                                String hex = bytesToHex(data);
                                System.out.println("SSL/TLS Handshake: " + hex);
                            } else {
                                // HTTPS 패킷 처리
                                System.out.println("HTTPS Packet: " + new String(data));
                                // System.out.println(packet);
                            }
                        }
                    }
                }
            }

            private String bytesToHex(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X ", b));
                }
                return sb.toString();
            }
        };
        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "");


    }
//    public void captureNaverPacket(Pcap pcap, Long userIdx,String domainName) {
//
//
//        //네이버 도메인 관련 필터 생성
//        String filter = "tcp port 443";
//        PcapBpfProgram program = new PcapBpfProgram();
//        int mask = 0xffffff;
//        if (pcap.compile(program, filter, 1, mask) != Pcap.OK) {
//            System.err.println(pcap.getErr());
//        }
//        if (pcap.setFilter(program) != Pcap.OK) {
//            System.err.println(pcap.getErr());
//        }
//
//        String addrNaver = null;
//
//        try {
//            //InetAddress address = InetAddress.getByName("www.naver.com");
//            InetAddress address = InetAddress.getByName(domainName);
//            //네이버의 IP 주소 223.130.195.95
//            addrNaver = address.getHostAddress();
//
//            System.out.println(domainName+"의 IP 주소: " + addrNaver);
//        } catch (Exception e) {
//            System.out.println("IP 주소를 얻는 중 오류가 발생하였습니다.");
//            e.printStackTrace();
//        }
//
//        // 도메인 IP 고정된 부분 추출하기
//        //223.130
//        String regex = "^(\\d{1,3}\\.\\d{1,3})\\..*";
//        String extractedAddr = null;
//
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(addrNaver);
//        System.out.println("pattern = " + pattern);
//        System.out.println("matcher = " + matcher);
//
//        if (matcher.matches()) { extractedAddr = matcher.group(1); }
//        System.out.println();
//
//        // 캡처된 패킷 처리
//        //finalExtractedAddr : 223.130
//        String finalExtractedAddr = extractedAddr;
//        System.out.println("finalExtractedAddr = " + finalExtractedAddr);
//
//        JPacketHandler<String> packetHandler = new JPacketHandler<>() {
//            private final Tcp tcp = new Tcp();
//            private final Ip4 ip = new Ip4();
//
//            int naverPacketNum=0; //naver 필터에 부합하는 몇 번째 필터인지
//
//            long firstPacketCapturedTime=0;//첫 네이버 패킷의 캡쳐된 시간
//            @Override
//            public void nextPacket(JPacket packet, String user) {
//                if (packet.hasHeader(tcp) && packet.hasHeader(ip)) {
//                    System.out.println("user = " + user);
//                    System.out.printf("캡처 시작: %s\n 패킷의 길이: %-4d\n", new Date(packet.getCaptureHeader().timestampInMillis()),
//                            packet.getCaptureHeader().caplen());
//
//                    //System.out.println("packet.toString() = " + packet.toString());
//                    String srcIp = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
//                    String dstIp = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());
//                    System.out.println("Source IP: " + srcIp);
//                    System.out.println("Destination IP: " + dstIp);
//                    //네이버로 오고가는 패킷 캡쳐 및 추출
//                    if (srcIp.startsWith(finalExtractedAddr) || dstIp.startsWith(finalExtractedAddr)) {
//
////                        try {
////                            InetAddress address = InetAddress.getByName("www.naver.com");
////                            //네이버의 IP 주소 223.130.195.95
////                             addrNaver = address.getHostAddress();
////
////                            System.out.println("네이버의 IP 주소: " + addrNaver);
////                        } catch (Exception e) {
////                            System.out.println("IP 주소를 얻는 중 오류가 발생하였습니다.");
////                            e.printStackTrace();
////                        }
//                        long usedTime; //사용시간 (단위는 우선 밀리세컨드, 추후 수정 가능)
//
//                        //첫번째 패킷의 캡쳐 시간은 계속 저장해놓기 위해 변수에 저장
//                        if(naverPacketNum ==0){
//                            firstPacketCapturedTime = packet.getCaptureHeader().timestampInMillis();
//                            System.out.println("naver Packet First Captured");
//                            System.out.println("firstPacketCapturedTime = " + firstPacketCapturedTime);
//                            //naverPacketNum++;
//                        }
//                        //지속해서 네이버 패킷을 캡쳐한 후 첫번째 네이버 패킷 캡쳐 시간과의 차를 usedTime 변수에 저장
//                        else{
//                            System.out.println("not first Time Captured ");
//                            long currentCapturedPacket = packet.getCaptureHeader().timestampInMillis();
//                            System.out.println("currentCapturedPacket = " + currentCapturedPacket);
//                            usedTime = currentCapturedPacket -firstPacketCapturedTime;
//                            usedTimeService.insertNaverUsedTime(userIdx, usedTime);
//                            System.out.println("usedTime = " + usedTime);
//                            //naverPacketNum++;
//                        }
//                        naverPacketNum++; //캡쳐된 네이버 패킷 수 증가
//
//                        byte[] data = packet.getByteArray(tcp.getOffset(), tcp.getHeaderLength() + tcp.getPayloadLength());
//                        System.out.println("packet = " + packet);
//                        long time = packet.getCaptureHeader().timestampInMillis();
//
//                        if (tcp.destination() == 443) {
//                            // SSL/TLS 핸드셰이크 패킷 처리
//                            String hex = bytesToHex(data);
//                            System.out.println("SSL/TLS Handshake: " + hex);
//                        } else {
//                            // HTTPS 패킷 처리
//                            System.out.println("HTTPS Packet: " + new String(data));
//                            // System.out.println(packet);
//                        }
//                    }
//                }
//            }
//
//            private String bytesToHex(byte[] bytes) {
//                StringBuilder sb = new StringBuilder();
//                for (byte b : bytes) {
//                    sb.append(String.format("%02X ", b));
//                }
//                return sb.toString();
//            }
//        };
//        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "");


  //  }
    public void captureHttpsPacket(Pcap pcap, Long userIdx) {


        String filter = "tcp port 443";
        PcapBpfProgram program = new PcapBpfProgram();
        int mask = 0xffffff;
        if (pcap.compile(program, filter, 1, mask) != Pcap.OK) {
            System.err.println(pcap.getErr());
        }
        if (pcap.setFilter(program) != Pcap.OK) {
            System.err.println(pcap.getErr());
        }

        // 캡처된 패킷 처리
        JPacketHandler<String> packetHandler = new JPacketHandler<>() {
            private final Tcp tcp = new Tcp();
            private final Ip4 ip = new Ip4();

            @Override
            public void nextPacket(JPacket packet, String user) {
                if (packet.hasHeader(tcp) && packet.hasHeader(ip)) {
                    String srcIp = org.jnetpcap.packet.format.FormatUtils.ip(ip.source());
                    String dstIp = org.jnetpcap.packet.format.FormatUtils.ip(ip.destination());
                    System.out.println("Source IP: " + srcIp);
                    System.out.println("Destination IP: " + dstIp);
                    byte[] data = packet.getByteArray(tcp.getOffset(), tcp.getHeaderLength() + tcp.getPayloadLength());

//                    System.out.println(packet);
                    if (tcp.destination() == 443) {
                        // SSL/TLS 핸드셰이크 패킷 처리
                        String hex = bytesToHex(data);
                        System.out.println("SSL/TLS Handshake: " + hex);
                    } else {
                        // HTTPS 패킷 처리
                        System.out.println("HTTPS Packet: " + new String(data));
                        System.out.println("packet = " + packet);
                    }
                }
            }

            private String bytesToHex(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X ", b));
                }
                return sb.toString();
            }
        };
        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "");

    }

    public void captureHttpPacket() {

        String filter = "tcp port 80";
        PcapBpfProgram program = new PcapBpfProgram();
        int mask = 0xffffff;
        if (pcap.compile(program, filter, 1, mask) != Pcap.OK) {
            System.err.println(pcap.getErr());
        }
        if (pcap.setFilter(program) != Pcap.OK) {
            System.err.println(pcap.getErr());
        }

        // 캡처된 패킷 처리
        JPacketHandler<String> packetHandler = new JPacketHandler<>() {
            private final Tcp tcp = new Tcp();
            private final Ip4 ip = new Ip4();

            @Override
            public void nextPacket(JPacket packet, String user) {
                if (packet.hasHeader(tcp) && packet.hasHeader(ip)) {
                    byte[] data = packet.getByteArray(tcp.getOffset(), tcp.getHeaderLength() + tcp.getPayloadLength());


                    if (tcp.destination() == 80) {
                        // SSL/TLS 핸드셰이크 패킷 처리
                        String hex = bytesToHex(data);
                        System.out.println("SSL/TLS Handshake: " + hex);
                    } else {
                        // HTTPS 패킷 처리
                        System.out.println(packet);
                        System.out.println("HTTPS Packet: " + new String(data));
                    }
                }
            }

            private String bytesToHex(byte[] bytes) {
                StringBuilder sb = new StringBuilder();
                for (byte b : bytes) {
                    sb.append(String.format("%02X ", b));
                }
                return sb.toString();
            }
        };
        pcap.loop(Pcap.LOOP_INFINITE, packetHandler, "");

    }

    public void close() {
        pcap.close();
    }
}

