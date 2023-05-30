package com.example.domain.timeGoal.api;

import com.example.domain.timeGoal.dto.GetTimeGoalRes;
import com.example.domain.timeGoal.dto.PostTimeGoalReq;
import com.example.domain.timeGoal.dto.PostTimeGoalRes;
import com.example.domain.timeGoal.service.TimeGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/TimeGoal")
public class TimeGoalController {

    private final TimeGoalService usedTimeService;

    //1.도메인 별 시간 목표 생성 API
    @PostMapping("/goals/create/{userId}")
    public PostTimeGoalRes createGoalsByDomainName(
            @RequestBody PostTimeGoalReq postTimeGoals, @PathVariable(name="userId")Long userIdx){
        return usedTimeService.createGoalsByDomainName(postTimeGoals, userIdx);
    }

    //2.현재까지 저장된 모든 도메인 별 시간 목표 조회 API
    @GetMapping("/goals/read/{userId}")
    public List<GetTimeGoalRes> getAllGoalsByUser(@PathVariable(name = "userId")Long userIdx){
        return usedTimeService.getAllGoalsByUser(userIdx);
    }
}
