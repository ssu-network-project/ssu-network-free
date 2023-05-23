package com.example.domain.usedTime.api;


import com.example.domain.usedTime.dto.GetUsedTimeRes;
import com.example.domain.usedTime.service.UsedTimeService;
import com.example.global.JPacketCapture;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/UsedTime")
public class UsedTimeController {

    private final UsedTimeService usedTimeService;
    private final JPacketCapture jPacketCapture;
    @GetMapping("/capture/{userId}")
    public void capturePacket(@PathVariable(name = "userId") Long userIdx) throws IOException, ExecutionException, InterruptedException {
        jPacketCapture.startCaptureByInterface(userIdx);
    }

    @GetMapping("/all/{userId}")
    public List<GetUsedTimeRes> getAllUsedTime(@PathVariable(name = "userId") Long userIdx){
        return usedTimeService.getAllUsedTime(userIdx);
    }

}
