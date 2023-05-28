package com.example.domain.usedTime.api;


import com.example.domain.usedTime.service.UsedTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/UsedTime")
public class UsedTimeController {

    private final UsedTimeService usedTimeService;

    @GetMapping("/capture/multithread/{userId}")
    public void capturePacketMultiThread(@PathVariable(name="userId") Long userIdx) {
        usedTimeService.capturePacketMultiThread(userIdx);
    }

    @GetMapping("/save/{userId}")
    public void saveUsageToDB(@PathVariable(name="userId") Long userIdx) {
        usedTimeService.saveUsageToDB(userIdx);
    }
}
